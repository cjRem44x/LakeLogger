package com.lakelogger.dao;

import com.lakelogger.model.CatchEntry;
import com.lakelogger.model.Location;
import com.lakelogger.model.Bait;
import com.lakelogger.util.DateTimeUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for catch entries and related data.
 */
public class CatchDAO {

    private final DatabaseManager dbManager;

    public CatchDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // ==================== Catch Entry Operations ====================

    public int saveCatch(CatchEntry entry) {
        String sql = """
            INSERT INTO catches (catch_date, catch_time, bass_length_inches, bass_weight_lbs,
                                 location, weather, temperature_f, bait, notes, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, DateTimeUtil.formatDate(entry.getCatchDate()));
            pstmt.setString(2, DateTimeUtil.formatTime(entry.getCatchTime()));
            pstmt.setDouble(3, entry.getBassLengthInches());
            pstmt.setDouble(4, entry.getBassWeightLbs());
            pstmt.setString(5, entry.getLocation());
            pstmt.setString(6, entry.getWeather());
            pstmt.setInt(7, entry.getTemperatureF());
            pstmt.setString(8, entry.getBait());
            pstmt.setString(9, entry.getNotes());
            pstmt.setString(10, DateTimeUtil.formatDateTime(entry.getCreatedAt()));

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                entry.setId(id);

                // Also save the location if new
                if (entry.getLocation() != null && !entry.getLocation().isEmpty()) {
                    saveLocation(new Location(entry.getLocation()));
                }

                return id;
            }

        } catch (SQLException e) {
            System.err.println("Error saving catch: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateCatch(CatchEntry entry) {
        String sql = """
            UPDATE catches SET catch_date = ?, catch_time = ?, bass_length_inches = ?,
                               bass_weight_lbs = ?, location = ?, weather = ?,
                               temperature_f = ?, bait = ?, notes = ?
            WHERE id = ?
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, DateTimeUtil.formatDate(entry.getCatchDate()));
            pstmt.setString(2, DateTimeUtil.formatTime(entry.getCatchTime()));
            pstmt.setDouble(3, entry.getBassLengthInches());
            pstmt.setDouble(4, entry.getBassWeightLbs());
            pstmt.setString(5, entry.getLocation());
            pstmt.setString(6, entry.getWeather());
            pstmt.setInt(7, entry.getTemperatureF());
            pstmt.setString(8, entry.getBait());
            pstmt.setString(9, entry.getNotes());
            pstmt.setInt(10, entry.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating catch: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCatch(int id) {
        String sql = "DELETE FROM catches WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting catch: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public CatchEntry getCatchById(int id) {
        String sql = "SELECT * FROM catches WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCatch(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting catch: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<CatchEntry> getAllCatches() {
        return getCatchesWithFilter(null, null, null, null);
    }

    public List<CatchEntry> getCatchesWithFilter(LocalDate startDate, LocalDate endDate,
                                                   String location, String bait) {
        StringBuilder sql = new StringBuilder("SELECT * FROM catches WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (startDate != null) {
            sql.append(" AND catch_date >= ?");
            params.add(DateTimeUtil.formatDate(startDate));
        }
        if (endDate != null) {
            sql.append(" AND catch_date <= ?");
            params.add(DateTimeUtil.formatDate(endDate));
        }
        if (location != null && !location.isEmpty() && !location.equals("All")) {
            sql.append(" AND location = ?");
            params.add(location);
        }
        if (bait != null && !bait.isEmpty() && !bait.equals("All")) {
            sql.append(" AND bait = ?");
            params.add(bait);
        }

        sql.append(" ORDER BY catch_date DESC, catch_time DESC");

        List<CatchEntry> catches = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                catches.add(mapResultSetToCatch(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting catches: " + e.getMessage());
            e.printStackTrace();
        }

        return catches;
    }

    private CatchEntry mapResultSetToCatch(ResultSet rs) throws SQLException {
        CatchEntry entry = new CatchEntry();
        entry.setId(rs.getInt("id"));
        entry.setCatchDate(DateTimeUtil.parseDate(rs.getString("catch_date")));
        entry.setCatchTime(DateTimeUtil.parseTime(rs.getString("catch_time")));
        entry.setBassLengthInches(rs.getDouble("bass_length_inches"));
        entry.setBassWeightLbs(rs.getDouble("bass_weight_lbs"));
        entry.setLocation(rs.getString("location"));
        entry.setWeather(rs.getString("weather"));
        entry.setTemperatureF(rs.getInt("temperature_f"));
        entry.setBait(rs.getString("bait"));
        entry.setNotes(rs.getString("notes"));
        entry.setCreatedAt(DateTimeUtil.parseDateTime(rs.getString("created_at")));
        return entry;
    }

    // ==================== Location Operations ====================

    public void saveLocation(Location location) {
        String sql = "INSERT OR IGNORE INTO locations (name, description) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, location.getName());
            pstmt.setString(2, location.getDescription());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving location: " + e.getMessage());
        }
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM locations ORDER BY name";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                locations.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting locations: " + e.getMessage());
        }

        // Also get locations from catches table that might not be in locations table
        String catchLocSql = "SELECT DISTINCT location FROM catches WHERE location IS NOT NULL AND location != ''";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(catchLocSql)) {

            while (rs.next()) {
                String loc = rs.getString("location");
                if (!locations.contains(loc)) {
                    locations.add(loc);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting catch locations: " + e.getMessage());
        }

        locations.sort(String::compareToIgnoreCase);
        return locations;
    }

    // ==================== Bait Operations ====================

    public void saveBait(Bait bait) {
        String sql = "INSERT OR IGNORE INTO baits (name, type) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bait.getName());
            pstmt.setString(2, bait.getType());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving bait: " + e.getMessage());
        }
    }

    public List<String> getAllBaits() {
        List<String> baits = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM baits ORDER BY name";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                baits.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting baits: " + e.getMessage());
        }

        return baits;
    }

    // ==================== Export Operations ====================

    public String exportToCsv(List<CatchEntry> catches) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Time,Length (in),Weight (lbs),Location,Weather,Temp (F),Bait,Notes\n");

        for (CatchEntry c : catches) {
            csv.append(String.format("\"%s\",\"%s\",%.1f,%.2f,\"%s\",\"%s\",%d,\"%s\",\"%s\"\n",
                    c.getCatchDate() != null ? c.getCatchDate() : "",
                    c.getCatchTime() != null ? c.getCatchTime() : "",
                    c.getBassLengthInches(),
                    c.getBassWeightLbs(),
                    c.getLocation() != null ? c.getLocation() : "",
                    c.getWeather() != null ? c.getWeather() : "",
                    c.getTemperatureF(),
                    c.getBait() != null ? c.getBait() : "",
                    c.getNotes() != null ? c.getNotes().replace("\"", "\"\"") : ""));
        }

        return csv.toString();
    }
}
