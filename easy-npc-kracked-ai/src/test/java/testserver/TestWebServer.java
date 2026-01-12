package testserver;

import com.ainpcconnector.config.ModConfig;
import com.ainpcconnector.web.WebServer;

/**
 * Standalone test server for the web interface.
 * This runs the Javalin web server without requiring Minecraft to be running.
 */
public class TestWebServer {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Easy NPC kracked AI - Web Test Server");
        System.out.println("========================================");
        System.out.println();

        // Create config with test settings
        ModConfig config = new ModConfig();
        config.getWebServer().setEnabled(true);
        config.getWebServer().setPort(8081);
        config.getWebServer().setIp("0.0.0.0");

        System.out.println("Test configuration loaded:");
        System.out.println("  - Web Server: enabled");
        System.out.println("  - Port: 8081");
        System.out.println("  - Bind IP: 0.0.0.0");
        System.out.println();

        // Create and start web server
        WebServer webServer = new WebServer(config);
        webServer.start();

        System.out.println();
        System.out.println("========================================");
        System.out.println("  Web server is running!");
        System.out.println("  Open your browser to: http://localhost:8081");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Press Ctrl+C to stop the server.");
        System.out.println();

        // Keep the application running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Shutting down web server...");
            webServer.stop();
            System.out.println("Web server stopped.");
        }));

        // Wait indefinitely
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // Shutdown
        }
    }
}
