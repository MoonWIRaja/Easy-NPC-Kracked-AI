package com.ainpcconnector.web.handlers;

import com.ainpcconnector.web.auth.AuthenticationService;
import com.ainpcconnector.web.auth.User;
import io.javalin.http.Context;

import java.util.Map;

/**
 * Handler for authentication endpoints.
 */
public class AuthHandler {

    private final AuthenticationService authService;

    public AuthHandler(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Handle login request.
     * POST /api/auth/login
     * Body: { "username": "...", "password": "..." }
     */
    public void handleLogin(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

            if (request.username == null || request.username.isEmpty() ||
                    request.password == null || request.password.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Username and password are required"));
                return;
            }

            AuthenticationService.LoginResult result = authService.login(request.username, request.password);

            if (result.isSuccess()) {
                ctx.json(Map.of(
                        "token", result.getToken(),
                        "user", Map.of(
                                "id", result.getUser().getId().toString(),
                                "username", result.getUser().getUsername(),
                                "role", result.getUser().getRole().name()
                        )
                ));
            } else {
                ctx.status(401).json(Map.of("error", result.getError()));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Handle logout request.
     * POST /api/auth/logout
     */
    public void handleLogout(Context ctx) {
        String token = extractToken(ctx);
        if (token != null) {
            authService.logout(token);
        }
        ctx.json(Map.of("message", "Logged out successfully"));
    }

    /**
     * Get current user info.
     * GET /api/auth/me
     */
    public void handleMe(Context ctx) {
        User user = ctx.attribute("user");
        ctx.json(Map.of(
                "id", user.getId().toString(),
                "username", user.getUsername(),
                "role", user.getRole().name()
        ));
    }

    /**
     * Get all users (admin only).
     * GET /api/users
     */
    public void handleGetUsers(Context ctx) {
        ctx.json(authService.getAllUsers());
    }

    /**
     * Create a new user (admin only).
     * POST /api/users
     * Body: { "username": "...", "password": "...", "role": "ADMIN|USER" }
     */
    public void handleCreateUser(Context ctx) {
        try {
            CreateUserRequest request = ctx.bodyAsClass(CreateUserRequest.class);

            if (request.username == null || request.username.isEmpty() ||
                    request.password == null || request.password.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Username and password are required"));
                return;
            }

            User.UserRole role = User.UserRole.USER;
            if (request.role != null) {
                try {
                    role = User.UserRole.valueOf(request.role.toUpperCase());
                } catch (IllegalArgumentException e) {
                    ctx.status(400).json(Map.of("error", "Invalid role. Must be ADMIN or USER"));
                    return;
                }
            }

            User user = authService.createUser(request.username, request.password, role);

            ctx.status(201).json(Map.of(
                    "id", user.getId().toString(),
                    "username", user.getUsername(),
                    "role", user.getRole().name()
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Delete a user (admin only).
     * DELETE /api/users/{id}
     */
    public void handleDeleteUser(Context ctx) {
        try {
            String userIdStr = ctx.pathParam("id");
            java.util.UUID userId = java.util.UUID.fromString(userIdStr);

            if (authService.deleteUser(userId)) {
                ctx.json(Map.of("message", "User deleted successfully"));
            } else {
                ctx.status(404).json(Map.of("error", "User not found"));
            }
        } catch (java.lang.IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid user ID format"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    private String extractToken(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return ctx.queryParam("token");
    }

    public record LoginRequest(String username, String password) {}
    public record CreateUserRequest(String username, String password, String role) {}
}
