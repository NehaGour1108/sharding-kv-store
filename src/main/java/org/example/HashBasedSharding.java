package org.example;

import java.sql.*;
import java.lang.String;

public class HashBasedSharding {
    private static final int NUM_SHARDS = 3; // Number of shards
    private static final String USER = "root"; // MySQL user
    private static final String PASSWORD = ""; // MySQL password

    // Method to calculate the shard based on the hash of the key
    private int getShard(String key) {
        return Math.abs(key.hashCode()) % NUM_SHARDS; // Modulo to distribute keys across shards
    }

    // Method to create a connection to the specific shard
    private Connection getConnection(int shard) throws SQLException {
        String url = "jdbc:mysql://localhost:330" + (shard + 6) + "/shard" + shard; // Assuming shards on different ports
        return DriverManager.getConnection(url, "root", "Neha11@S");
    }

    // Method to insert key-value pair into the appropriate shard
    public void put(String key, String value) {
        try {
            int shard = getShard(key);
            try (Connection connection = getConnection(shard)) {
                createTableIfNotExists(connection); // Ensure the table exists in the shard
                String query = "INSERT INTO kv_store (key, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, key);
                    stmt.setString(2, value);
                    stmt.setString(3, value);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve value based on the key from the appropriate shard
    public String get(String key) {
        try {
            int shard = getShard(key);
            try (Connection connection = getConnection(shard)) {
                String query = "SELECT value FROM kv_store WHERE key = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, key);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getString("value");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to create the table if it doesn't already exist
    private void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS kv_store (key VARCHAR(255) PRIMARY KEY, value TEXT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public static void main(String[] args) {
        HashBasedSharding sharding = new HashBasedSharding();

        // Example of putting key-value pairs into different shards
        sharding.put("key1", "value1");
        sharding.put("key2", "value2");
        sharding.put("key3", "value3");

        // Example of getting values from different shards
        System.out.println("Value for key1: " + sharding.get("key1"));
        System.out.println("Value for key2: " + sharding.get("key2"));
        System.out.println("Value for key3: " + sharding.get("key3"));
    }
}
