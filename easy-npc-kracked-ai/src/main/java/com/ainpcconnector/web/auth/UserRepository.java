package com.ainpcconnector.web.auth;

import com.ainpcconnector.AINpcConnectorMod;
import com.ainpcconnector.config.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for user data persistence using SQLite.
 */
public class UserRepository {

    private final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> usersByUsername = new ConcurrentHashMap<>();
    private DatabaseManager database;

    /**
     * Create repository with default path (Fabric config dir or current directory for testing).
     */
    public UserRepository() {
        // Try to get database from ConfigManager if available
        try {
            database = com.ainpcconnector.AINpcConnectorMod.getConfigManager().getDatabase();
            loadFromDatabase();
        } catch (Exception e) {
            AINpcConnectorMod.LOGGER.warn("[AI NPC Connector] Database not available, using in-memory storage");
        }
    }

    /**
     * Set database manager (for dependency injection).
     */
    public void setDatabase(DatabaseManager database) {
        this.database = database;
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        if (database == null) return;

        List<DatabaseManager.UserRecord> records = database.getAllUsers();
        for (DatabaseManager.UserRecord record : records) {
            User user = new User(
                UUID.randomUUID(), // Generate new ID
                record.username(),
                record.passwordHash(),
                User.UserRole.valueOf(record.role())
            );
            users.put(user.getId(), user);
            usersByUsername.put(user.getUsername(), user);
        }
        AINpcConnectorMod.LOGGER.info("[AI NPC Connector] Loaded {} users from database", users.size());
    }

    public void load() {
        // Loading is now done in constructor via loadFromDatabase()
        if (database == null) {
            AINpcConnectorMod.LOGGER.warn("[AI NPC Connector] No database available for user loading");
        }
    }

    public void save() {
        // Saving is done immediately on each operation
    }

    public User save(User user) {
        users.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);

        if (database != null) {
            DatabaseManager.UserRecord record = new DatabaseManager.UserRecord(
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole().name(),
                java.time.Instant.now().toString()
            );
            database.saveUser(record);
        }

        return user;
    }

    public User findByUsername(String username) {
        return usersByUsername.get(username);
    }

    public User findById(UUID id) {
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(UUID id) {
        User user = users.remove(id);
        if (user != null) {
            usersByUsername.remove(user.getUsername());

            if (database != null) {
                database.deleteUser(user.getUsername());
            }
        }
    }

    public int count() {
        return users.size();
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}
