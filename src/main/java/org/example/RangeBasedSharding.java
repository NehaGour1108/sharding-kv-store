package org.example;

import java.sql.*;

public class RangeBasedSharding {
    private static final int NUM_SHARDS = 3;
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Method to determine which shard to route based on key
    private int getShard(String key) {
        // In this example, we're using the ASCII value of the first character of the key for range-based sharding
        char firstChar = key.charAt(0);
        if (firstChar < 'M') {
            return 0; // Shard 0
        } else if (firstChar < 'T') {
            return 1; // Shard 1
        } else {
            return 2; // Shard 2
        }
    }

    private Connection getConnection(int shard) throws SQLException {
        String url = "jdbc:mysql://localhost:330" + (shard + 6) + "/shard" + shard;
        return DriverManager.getConnection(url, USER, PASSWORD);
    }

    public void put(String key, String value) {
        try {
            int shard = getShard(key);
            try (Connection connection = getConnection(shard)) {
                createTableIfNotExists(connection);
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

    private void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS kv_store (key VARCHAR(255) PRIMARY KEY, value TEXT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public static void main(String[] args) {
        RangeBasedSharding sharding = new RangeBasedSharding();

        // Insert key-value pairs
        sharding.put("apple", "fruit1");
        sharding.put("banana", "fruit2");
        sharding.put("zebra", "animal1");

        // Retrieve values
        System.out.println("Value for apple: " + sharding.get("apple"));
        System.out.println("Value for banana: " + sharding.get("banana"));
        System.out.println("Value for zebra: " + sharding.get("zebra"));
    }
}
