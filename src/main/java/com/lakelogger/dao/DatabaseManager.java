package com.lakelogger.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

/**
 * Manages SQLite database connections and initialization.
 */
public class DatabaseManager {

    private static final String DB_NAME = "lakelogger.db";
    private static final String DB_URL;
    private static DatabaseManager instance;

    static {
        // Store database in user's home directory under .lakelogger
        String userHome = System.getProperty("user.home");
        File dbDir = new File(userHome, ".lakelogger");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        DB_URL = "jdbc:sqlite:" + new File(dbDir, DB_NAME).getAbsolutePath();
    }

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create catches table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS catches (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    catch_date TEXT NOT NULL,
                    catch_time TEXT,
                    bass_length_inches REAL,
                    bass_weight_lbs REAL,
                    location TEXT,
                    weather TEXT,
                    temperature_f INTEGER,
                    bait TEXT,
                    notes TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create locations table for dropdown management
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS locations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    description TEXT
                )
            """);

            // Create baits table for dropdown management
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS baits (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    type TEXT
                )
            """);

            // Insert default baits if table is empty
            stmt.execute("""
                INSERT OR IGNORE INTO baits (name, type) VALUES
                ('Plastic Worm', 'Soft Plastic'),
                ('Senko', 'Soft Plastic'),
                ('Crankbait', 'Hard Bait'),
                ('Spinnerbait', 'Hard Bait'),
                ('Jig', 'Jig'),
                ('Topwater Frog', 'Topwater'),
                ('Buzzbait', 'Topwater'),
                ('Swimbait', 'Soft Plastic'),
                ('Texas Rig', 'Soft Plastic'),
                ('Drop Shot', 'Finesse'),
                ('Ned Rig', 'Finesse'),
                ('Chatterbait', 'Hard Bait'),
                ('Lipless Crankbait', 'Hard Bait'),
                ('Jerkbait', 'Hard Bait'),
                ('Live Bait', 'Live')
            """);

            System.out.println("Database initialized successfully at: " + DB_URL);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the database file path for display purposes.
     */
    public String getDatabasePath() {
        return DB_URL.replace("jdbc:sqlite:", "");
    }
}
