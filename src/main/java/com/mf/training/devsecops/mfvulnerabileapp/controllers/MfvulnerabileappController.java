package com.mf.training.devsecops.mfvulnerabilepp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
public class MfvulnerabileappController {

    private final String url = "jdbc:mysql://localhost/test?autoReconnect=true&useSSL=false"; // Use of insecure connection parameters
    private final String user = "admin";
    private final String password = "admin123"; // Hardcoded credentials

    private static final Logger logger = Logger.getLogger(SuperVulnerableController.class.getName());

    // SQL Injection
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable String id) {
        String query = "SELECT name FROM users WHERE id = '" + id + "'";
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception: ", e); // Logging sensitive exception details
        }
        return "User not founcd";
    }

    // Cross-Site Scripting (XSS)
    @GetMapping("/greet")
    public String greetUser(@RequestParam String name) {
        return "<html>Hello, " + name + "!</html>"; // Directly embedding user input in HTML response
    }

    // Command Injection
    @GetMapping("/run")
    public String runCommand(@RequestParam String command) {
        try {
            Runtime.getRuntime().exec(command); // Executing command received from user input
            return "Command executed";
        } catch (Exception e) {
            return "Command execution failed";
        }
    }

    // Insecure File Upload
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") String fileContent) {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/uploadedFile.txt"), fileContent.getBytes()); // Writing user-provided content to a static file path
            return "File uploaded successfully";
        } catch (Exception e) {
            return "File upload failed";
        }
    }

    // Missing Authentication for a Critical Function
    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable String id) {
        // Assume this method deletes a user from the database without checking the user's authentication
        return "User deleted";
    }

    // Use of a Broken or Risky Cryptographic Algorithm
    @GetMapping("/encrypt")
    public String encryptData(@RequestParam String data) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES"); // Using DES, which is considered a weak encryption
            // Encryption logic here...
            return "Data encrypted";
        } catch (Exception e) {
            return "Encryption failed";
        }
    }

    // Excessive Logging of Sensitive Information
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        logger.info("Attempting login for username: " + username + " with password: " + password); // Logging sensitive information
        // Authentication logic here...
        return "Logged in";
    }
}