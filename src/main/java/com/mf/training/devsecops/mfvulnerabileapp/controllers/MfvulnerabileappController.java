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

    private final String url = "jdbc:mysql://localhost/test";
    private final static String user = "roodt";
    private final static String password = "password";

    // SQL Injection Vulnerability
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
            e.printStackTrace();
        }
        return "User not found";
    }

    // Insecure Deserialization Vulnerability
    @GetMapping("/deserialize/{object}")
    public String deserializeObject(@PathVariable String object) {
        try {
            // Example of insecure deserialization
            byte[] data = java.util.Base64.getDecoder().decode(object);
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data));
            Object o = ois.readObject();
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
            // Example of path traversal vulnerability
            java.nio.file.Path path = java.nio.file.Paths.get("/tmp/", name);
            return new String(java.nio.file.Files.readAllBytes(path));
        } catch (java.io.IOException e) {
            return "Error reading file";
        }
    }
}