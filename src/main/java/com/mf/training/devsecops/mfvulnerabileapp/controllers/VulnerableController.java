package com.mf.training.devsecops.mfvulnerabileapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
public class VulnerableController {

    private final String url = "jdbc:mysql://localhost/test?autoReconnect=true&useSSL=false"; // Hardcoded database credentials
    private final String user = "root"; // Sensitive data exposure
    private final String password = "password"; // Sensitive data exposure

    // SQL Injection Vulnerability
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable String id) {
        String query = "SELECT name FROM users WHERE id = '" + id + "'"; // SQL Injection
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Improper exception handling
        }
        return "User not found";
    }

    // Insecure Deserialization Vulnerability
    @GetMapping("/deserialize/{object}")
    public String deserializeObject(@PathVariable String object) {
        try {
            byte[] data = java.util.Base64.getDecoder().decode(object);
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data));
            Object o = ois.readObject(); // Insecure Deserialization
            ois.close();
            return "Object deserialized: " + o.toString();
        } catch (Exception e) {
            return "Deserialization failed";
        }
    }

    // Path Traversal Vulnerability
    @GetMapping("/file/{name}")
    public String readFile(@PathVariable String name) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("/tmp/", name); // Path Traversal
            return new String(java.nio.file.Files.readAllBytes(path));
        } catch (java.io.IOException e) {
            return "Error reading file";
        }
    }

    // Unvalidated Redirects and Forwards
    @GetMapping("/redirect/{url}")
    public void redirectUser(@PathVariable String url) {
        // This is an example of an unvalidated redirect vulnerability
        // NEVER use in production code
        org.springframework.web.servlet.view.RedirectView redirectView = new org.springframework.web.servlet.view.RedirectView();
        redirectView.setUrl(url); // Unvalidated Redirect
    }

    // Hardcoded cryptographic key
    private static final String secretKey = "hardcodedkey"; // Hardcoded cryptographic key

    // Use of == for String comparison
    @GetMapping("/compare/{input}")
    public boolean compareStrings(@PathVariable String input) {
        return input == "test"; // Incorrect string comparison using ==
    }

    // Non-final field in a class marked with @RequestMapping or derivative
    private String nonFinalField = "This should be final or not mutable";

    // Public array field
    public byte[] publicArray = new byte[10]; // Public mutable array field
}