package com.ainpcconnector.config;

import com.ainpcconnector.AINpcConnectorMod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SQLite Database Manager for persistent storage.
 * Stores: Config, Users, AI Providers, NPC Profiles
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private final Connection connection;
    private final ReentrantLock lock = new ReentrantLock();

    private DatabaseManager(File dbFile) throws SQLException {

        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        initializeTables();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized. Call init() first.");
        }
        return instance;
    }

    public static void init(File configDir) {
        if (instance != null)
            return;

        try {
            File dbFile = new File(configDir, "ainpc_data.db");
            instance = new DatabaseManager(dbFile);
            AINpcConnectorMod.LOGGER
                    .info("[AI NPC Connector] SQLite database initialized: " + dbFile.getAbsolutePath());
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Config table - stores main config as JSON
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS config (
                            key TEXT PRIMARY KEY,
                            value TEXT NOT NULL
                        )
                    """);

            // AI Providers table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS ai_providers (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            api_key TEXT,
                            endpoint TEXT NOT NULL,
                            model TEXT NOT NULL
                        )
                    """);

            // Users table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            username TEXT PRIMARY KEY,
                            password_hash TEXT NOT NULL,
                            role TEXT NOT NULL,
                            created_at TEXT NOT NULL
                        )
                    """);

            // NPC Profiles table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS npc_profiles (
                            entity_uuid TEXT PRIMARY KEY,
                            entity_name TEXT NOT NULL,
                            ai_enabled INTEGER DEFAULT 0,
                            voice_enabled INTEGER DEFAULT 0,
                            ai_provider_id TEXT,
                            personality TEXT,
                            system_prompt TEXT,
                            personality_traits TEXT,
                            conversation_history TEXT,
                            created_at TEXT NOT NULL,
                            updated_at TEXT NOT NULL
                        )
                    """);

            // Conversation table (stores chat history per NPC)
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS conversations (
                            id TEXT PRIMARY KEY,
                            npc_uuid TEXT NOT NULL,
                            role TEXT NOT NULL,
                            content TEXT NOT NULL,
                            timestamp TEXT NOT NULL,
                            FOREIGN KEY (npc_uuid) REFERENCES npc_profiles(entity_uuid) ON DELETE CASCADE
                        )
                    """);

            // Create indexes
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_conversations_npc ON conversations(npc_uuid)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_npc_provider ON npc_profiles(ai_provider_id)");
        }
    }

    // ==================== CONFIG METHODS ====================

    public String getConfigValue(String key) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT value FROM config WHERE key = ?")) {
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
            return null;
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting config value", e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void setConfigValue(String key, String value) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO config (key, value) VALUES (?, ?)")) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error setting config value", e);
        } finally {
            lock.unlock();
        }
    }

    // ==================== AI PROVIDER METHODS ====================

    public List<ModConfig.ProviderConfig> getAIProviders() {
        lock.lock();
        List<ModConfig.ProviderConfig> providers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ai_providers")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ModConfig.ProviderConfig provider = new ModConfig.ProviderConfig();
                provider.setId(rs.getString("id"));
                provider.setName(rs.getString("name"));
                provider.setApiKey(rs.getString("api_key"));
                provider.setEndpoint(rs.getString("endpoint"));
                provider.setModel(rs.getString("model"));
                providers.add(provider);
            }
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting AI providers", e);
        } finally {
            lock.unlock();
        }
        return providers;
    }

    public void saveAIProvider(ModConfig.ProviderConfig provider) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO ai_providers (id, name, api_key, endpoint, model) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, provider.getId());
            stmt.setString(2, provider.getName());
            stmt.setString(3, provider.getApiKey());
            stmt.setString(4, provider.getEndpoint());
            stmt.setString(5, provider.getModel());
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error saving AI provider", e);
        } finally {
            lock.unlock();
        }
    }

    public void deleteAIProvider(String id) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM ai_providers WHERE id = ?")) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error deleting AI provider", e);
        } finally {
            lock.unlock();
        }
    }

    // ==================== USER METHODS ====================

    public UserRecord getUser(String username) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserRecord(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("created_at"));
            }
            return null;
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting user", e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public List<UserRecord> getAllUsers() {
        lock.lock();
        List<UserRecord> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new UserRecord(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("created_at")));
            }
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting all users", e);
        } finally {
            lock.unlock();
        }
        return users;
    }

    public void saveUser(UserRecord user) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO users (username, password_hash, role, created_at) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.passwordHash());
            stmt.setString(3, user.role());
            stmt.setString(4, user.createdAt() != null ? user.createdAt() : java.time.Instant.now().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error saving user", e);
        } finally {
            lock.unlock();
        }
    }

    public void deleteUser(String username) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error deleting user", e);
        } finally {
            lock.unlock();
        }
    }

    // ==================== NPC PROFILE METHODS ====================

    public NPCProfileRecord getNPCProfile(UUID uuid) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM npc_profiles WHERE entity_uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new NPCProfileRecord(
                        UUID.fromString(rs.getString("entity_uuid")),
                        rs.getString("entity_name"),
                        rs.getBoolean("ai_enabled"),
                        rs.getBoolean("voice_enabled"),
                        rs.getString("ai_provider_id"),
                        rs.getString("personality"),
                        rs.getString("system_prompt"),
                        rs.getString("personality_traits"),
                        rs.getString("conversation_history"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"));
            }
            return null;
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting NPC profile", e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public List<NPCProfileRecord> getAllNPCProfiles() {
        lock.lock();
        List<NPCProfileRecord> profiles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM npc_profiles")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                profiles.add(new NPCProfileRecord(
                        UUID.fromString(rs.getString("entity_uuid")),
                        rs.getString("entity_name"),
                        rs.getBoolean("ai_enabled"),
                        rs.getBoolean("voice_enabled"),
                        rs.getString("ai_provider_id"),
                        rs.getString("personality"),
                        rs.getString("system_prompt"),
                        rs.getString("personality_traits"),
                        rs.getString("conversation_history"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")));
            }
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting all NPC profiles", e);
        } finally {
            lock.unlock();
        }
        return profiles;
    }

    public void saveNPCProfile(NPCProfileRecord profile) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("""
                    INSERT OR REPLACE INTO npc_profiles
                    (entity_uuid, entity_name, ai_enabled, voice_enabled, ai_provider_id,
                     personality, system_prompt, personality_traits, conversation_history,
                     created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
            String now = java.time.Instant.now().toString();
            stmt.setString(1, profile.uuid().toString());
            stmt.setString(2, profile.entityName());
            stmt.setBoolean(3, profile.aiEnabled());
            stmt.setBoolean(4, profile.voiceEnabled());
            stmt.setString(5, profile.aiProviderId());
            stmt.setString(6, profile.personality());
            stmt.setString(7, profile.systemPrompt());
            stmt.setString(8, profile.personalityTraits());
            stmt.setString(9, profile.conversationHistory());
            stmt.setString(10, profile.createdAt() != null ? profile.createdAt() : now);
            stmt.setString(11, now);
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error saving NPC profile", e);
        } finally {
            lock.unlock();
        }
    }

    public void deleteNPCProfile(UUID uuid) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM npc_profiles WHERE entity_uuid = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error deleting NPC profile", e);
        } finally {
            lock.unlock();
        }
    }

    // ==================== CONVERSATION METHODS ====================

    public void addConversationMessage(UUID npcUuid, String role, String content) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("""
                    INSERT INTO conversations (id, npc_uuid, role, content, timestamp)
                    VALUES (?, ?, ?, ?, ?)
                """)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, npcUuid.toString());
            stmt.setString(3, role);
            stmt.setString(4, content);
            stmt.setString(5, java.time.Instant.now().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error adding conversation message", e);
        } finally {
            lock.unlock();
        }
    }

    public List<ConversationMessage> getConversationHistory(UUID npcUuid, int limit) {
        lock.lock();
        List<ConversationMessage> messages = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM conversations WHERE npc_uuid = ? ORDER BY timestamp DESC LIMIT ?")) {
            stmt.setString(1, npcUuid.toString());
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new ConversationMessage(
                        rs.getString("role"),
                        rs.getString("content"),
                        rs.getString("timestamp")));
            }
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error getting conversation history", e);
        } finally {
            lock.unlock();
        }
        return messages;
    }

    public void clearConversationHistory(UUID npcUuid) {
        lock.lock();
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM conversations WHERE npc_uuid = ?")) {
            stmt.setString(1, npcUuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error clearing conversation history", e);
        } finally {
            lock.unlock();
        }
    }

    // ==================== UTILITY METHODS ====================

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            AINpcConnectorMod.LOGGER.error("[AI NPC Connector] Error closing database", e);
        }
    }

    public void backup(Path backupPath) throws SQLException {
        // Execute SQLite backup command
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT sql FROM sqlite_master WHERE sql NOT NULL")) {
            StringBuilder backup = new StringBuilder();
            while (rs.next()) {
                backup.append(rs.getString("sql")).append(";\n");
            }
            Files.writeString(backupPath, backup.toString());
        } catch (Exception e) {
            throw new SQLException("Backup failed", e);
        }
    }

    // ==================== RECORD CLASSES ====================

    public record UserRecord(
            String username,
            String passwordHash,
            String role,
            String createdAt) {
    }

    public record NPCProfileRecord(
            UUID uuid,
            String entityName,
            boolean aiEnabled,
            boolean voiceEnabled,
            String aiProviderId,
            String personality,
            String systemPrompt,
            String personalityTraits, // JSON string
            String conversationHistory, // JSON string
            String createdAt,
            String updatedAt) {
    }

    public record ConversationMessage(
            String role,
            String content,
            String timestamp) {
    }
}
