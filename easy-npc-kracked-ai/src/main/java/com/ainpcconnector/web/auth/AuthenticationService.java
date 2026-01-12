package com.ainpcconnector.web.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentication service for web interface.
 */
public class AuthenticationService {

    private static final String JWT_SECRET = System.getenv()
            .getOrDefault("AINPC_JWT_SECRET", "change-this-secret-in-production");
    private static final int TOKEN_VALIDITY_HOURS = 24;

    private final UserRepository userRepository;
    private final Algorithm jwtAlgorithm;
    private final Map<String, User> activeSessions = new ConcurrentHashMap<>();

    public AuthenticationService() {
        this.userRepository = new UserRepository();
        this.jwtAlgorithm = Algorithm.HMAC256(JWT_SECRET);
    }

    /**
     * Authenticate a user and return a JWT token.
     * First user automatically becomes admin.
     */
    public LoginResult login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            // First user becomes admin automatically
            if (userRepository.isEmpty()) {
                user = createUser(username, password, User.UserRole.ADMIN);
            } else {
                return LoginResult.failure("Invalid username or password");
            }
        }

        // Verify password
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPasswordHash());
        if (!result.verified) {
            return LoginResult.failure("Invalid username or password");
        }

        // Generate JWT token
        String token = JWT.create()
                .withSubject(username)
                .withClaim("userId", user.getId().toString())
                .withClaim("role", user.getRole().name())
                .withExpiresAt(Date.from(Instant.now().plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS)))
                .sign(jwtAlgorithm);

        activeSessions.put(token, user);

        return LoginResult.success(token, user);
    }

    /**
     * Create a new user (admin only).
     */
    public User createUser(String username, String password, User.UserRole role) {
        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user = new User(UUID.randomUUID(), username, passwordHash, role);
        return userRepository.save(user);
    }

    /**
     * Validate a JWT token and return the associated user.
     */
    public User validateToken(String token) {
        try {
            JWT.require(jwtAlgorithm).build().verify(token);
            return activeSessions.get(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * Check if a token belongs to an admin user.
     */
    public boolean isAdmin(String token) {
        User user = validateToken(token);
        return user != null && user.getRole() == User.UserRole.ADMIN;
    }

    /**
     * Logout a user by invalidating their token.
     */
    public void logout(String token) {
        activeSessions.remove(token);
    }

    /**
     * Get all users (admin only).
     */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Delete a user (admin only).
     */
    public boolean deleteUser(UUID userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            userRepository.delete(userId);
            // Remove from active sessions
            activeSessions.values().removeIf(u -> u.getId().equals(userId));
            return true;
        }
        return false;
    }

    /**
     * Change user password.
     */
    public boolean changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }

        BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), user.getPasswordHash());
        if (!result.verified) {
            return false;
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
        return true;
    }

    /**
     * Result of a login attempt.
     */
    public static class LoginResult {
        private final boolean success;
        private final String token;
        private final User user;
        private final String error;

        private LoginResult(boolean success, String token, User user, String error) {
            this.success = success;
            this.token = token;
            this.user = user;
            this.error = error;
        }

        public static LoginResult success(String token, User user) {
            return new LoginResult(true, token, user, null);
        }

        public static LoginResult failure(String error) {
            return new LoginResult(false, null, null, error);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }

        public String getError() {
            return error;
        }
    }
}
