package com.ainpcconnector.web;

import com.ainpcconnector.AINpcConnectorMod;
import com.ainpcconnector.config.ModConfig;
import com.ainpcconnector.config.ConfigManager;
import com.ainpcconnector.web.auth.AuthenticationService;
import com.ainpcconnector.web.handlers.*;
import com.ainpcconnector.npc.NPCRegistry;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.net.InetAddress;

/**
 * Embedded web server for the Easy NPC kracked AI mod.
 * Provides REST API and web UI for configuration.
 */
public class WebServer {

    private final ConfigManager configManager;
    private final ModConfig config;
    private final AuthenticationService authService;
    private Javalin javalin;

    /**
     * Create web server with config manager (for mod usage).
     */
    public WebServer(ConfigManager configManager) {
        this.configManager = configManager;
        this.config = configManager.getConfig();
        this.authService = new AuthenticationService();
    }

    /**
     * Create web server with config directly (for testing).
     */
    public WebServer(ModConfig config) {
        this.configManager = new ConfigManager();
        this.configManager.updateConfig(config);
        this.config = config;
        this.authService = new AuthenticationService();
    }

    /**
     * Start the web server.
     */
    public void start() {
        if (!config.getWebServer().isEnabled()) {
            System.out.println("[Easy NPC kracked AI] Web server is disabled in config");
            return;
        }

        String bindAddress = config.getWebServer().getIp();
        int port = config.getWebServer().getPort();

        // Default to 0.0.0.0 if IP not specified
        if (bindAddress == null || bindAddress.isEmpty()) {
            bindAddress = "0.0.0.0";
        }

        // Configure Jackson for JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        javalin = Javalin.create(serverConfig -> {
            serverConfig.jsonMapper(new JavalinJackson(objectMapper));
            serverConfig.showJavalinBanner = false;

            // Static files serving
            serverConfig.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/web";
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });
        });

        // Enable CORS using before handler for all routes
        javalin.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        javalin.options("/*", ctx -> {
            // Handle preflight requests
            ctx.status(200);
        });

        // API Routes
        javalin.get("/api/health", this::handleHealth);

        // Authentication routes (no auth required)
        javalin.post("/api/auth/login", ctx -> new AuthHandler(authService).handleLogin(ctx));
        javalin.post("/api/auth/logout", ctx -> new AuthHandler(authService).handleLogout(ctx));

        // Authenticated routes
        javalin.get("/api/auth/me", createAuthHandler(ctx -> new AuthHandler(authService).handleMe(ctx)));
        javalin.get("/api/users", createAuthHandler(ctx -> new AuthHandler(authService).handleGetUsers(ctx)));
        javalin.post("/api/users", createAdminHandler(ctx -> new AuthHandler(authService).handleCreateUser(ctx)));
        javalin.delete("/api/users/{id}",
                createAdminHandler(ctx -> new AuthHandler(authService).handleDeleteUser(ctx)));

        // NPC routes
        javalin.get("/api/npcs", createAuthHandler(ctx -> new NPCHandler().handleListNPCs(ctx)));
        javalin.get("/api/npcs/{id}", createAuthHandler(ctx -> new NPCHandler().handleGetNPC(ctx)));
        javalin.put("/api/npcs/{id}", createAuthHandler(ctx -> new NPCHandler().handleUpdateNPC(ctx)));
        javalin.delete("/api/npcs/{id}", createAuthHandler(ctx -> new NPCHandler().handleDeleteNPC(ctx)));

        // Config routes
        ConfigHandler configHandler = new ConfigHandler(configManager);
        javalin.get("/api/config", createAuthHandler(ctx -> configHandler.handleGetConfig(ctx)));
        javalin.put("/api/config", createAdminHandler(ctx -> configHandler.handleUpdateConfig(ctx)));

        // AI Provider routes
        javalin.get("/api/config/providers", createAuthHandler(ctx -> configHandler.handleGetProviders(ctx)));
        javalin.post("/api/config/providers", createAdminHandler(ctx -> configHandler.handleAddProvider(ctx)));
        javalin.put("/api/config/providers/{id}", createAdminHandler(ctx -> configHandler.handleUpdateProvider(ctx)));
        javalin.delete("/api/config/providers/{id}", createAdminHandler(ctx -> configHandler.handleDeleteProvider(ctx)));

        // AI test routes
        javalin.post("/api/ai/test", createAuthHandler(ctx -> new AIHandler(configManager).handleTestConnection(ctx)));

        // Start server
        javalin.start(bindAddress, port);

        String displayUrl = getDisplayUrl(bindAddress, port);
        AINpcConnectorMod.LOGGER.info("[Easy NPC kracked AI] Web server started at {}", displayUrl);
    }

    /**
     * Stop the web server.
     */
    public void stop() {
        if (javalin != null) {
            javalin.stop();
            AINpcConnectorMod.LOGGER.info("[Easy NPC kracked AI] Web server stopped");
        }
    }

    /**
     * Create a handler that requires authentication.
     */
    private Handler createAuthHandler(Handler delegate) {
        return ctx -> {
            String token = extractToken(ctx);
            if (token == null) {
                ctx.status(401).json(java.util.Map.of("error", "Unauthorized - No token provided"));
                return;
            }

            var user = authService.validateToken(token);
            if (user == null) {
                ctx.status(401).json(java.util.Map.of("error", "Unauthorized - Invalid token"));
                return;
            }

            // Store user in context for downstream handlers
            ctx.attribute("user", user);
            delegate.handle(ctx);
        };
    }

    /**
     * Create a handler that requires admin authentication.
     */
    private Handler createAdminHandler(Handler delegate) {
        return ctx -> {
            String token = extractToken(ctx);
            if (token == null) {
                ctx.status(401).json(java.util.Map.of("error", "Unauthorized - No token provided"));
                return;
            }

            if (!authService.isAdmin(token)) {
                ctx.status(403).json(java.util.Map.of("error", "Forbidden - Admin access required"));
                return;
            }

            var user = authService.validateToken(token);
            ctx.attribute("user", user);
            delegate.handle(ctx);
        };
    }

    /**
     * Extract JWT token from request.
     */
    private String extractToken(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Also check query parameter
        return ctx.queryParam("token");
    }

    /**
     * Health check endpoint (no auth required).
     */
    private void handleHealth(Context ctx) {
        var registry = NPCRegistry.getInstance();
        ctx.json(java.util.Map.of(
                "status", "ok",
                "modVersion", AINpcConnectorMod.MOD_VERSION,
                "npcCount", registry.size()));
    }

    /**
     * Get the display URL for the web server.
     */
    private String getDisplayUrl(String bindAddress, int port) {
        if ("0.0.0.0".equals(bindAddress)) {
            try {
                String localIp = InetAddress.getLocalHost().getHostAddress();
                return "http://" + localIp + ":" + port;
            } catch (Exception e) {
                return "http://localhost:" + port;
            }
        }
        return "http://" + bindAddress + ":" + port;
    }

    public AuthenticationService getAuthService() {
        return authService;
    }
}
