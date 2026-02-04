package com.lakelogger.service;

import com.lakelogger.dao.CatchDAO;
import com.lakelogger.model.CatchEntry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating fishing analytics and statistics.
 */
public class AnalyticsService {

    private final CatchDAO catchDAO;

    public AnalyticsService() {
        this.catchDAO = new CatchDAO();
    }

    /**
     * Get bait statistics: count and average weight by bait type.
     */
    public Map<String, BaitStats> getBaitAnalysis() {
        List<CatchEntry> catches = catchDAO.getAllCatches();
        Map<String, BaitStats> stats = new LinkedHashMap<>();

        Map<String, List<CatchEntry>> byBait = catches.stream()
                .filter(c -> c.getBait() != null && !c.getBait().isEmpty())
                .collect(Collectors.groupingBy(CatchEntry::getBait));

        for (Map.Entry<String, List<CatchEntry>> entry : byBait.entrySet()) {
            List<CatchEntry> baitCatches = entry.getValue();
            int count = baitCatches.size();
            double avgWeight = baitCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .average().orElse(0);
            double avgLength = baitCatches.stream()
                    .mapToDouble(CatchEntry::getBassLengthInches)
                    .average().orElse(0);
            double maxWeight = baitCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .max().orElse(0);

            stats.put(entry.getKey(), new BaitStats(count, avgWeight, avgLength, maxWeight));
        }

        // Sort by count descending
        return stats.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().count, a.getValue().count))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Get time of day analysis.
     */
    public Map<String, TimeStats> getTimeOfDayAnalysis() {
        List<CatchEntry> catches = catchDAO.getAllCatches();
        Map<String, TimeStats> stats = new LinkedHashMap<>();

        // Initialize all time periods
        for (String period : Arrays.asList("Morning", "Midday", "Evening", "Night")) {
            stats.put(period, new TimeStats(0, 0, 0, 0));
        }

        Map<String, List<CatchEntry>> byTime = catches.stream()
                .filter(c -> c.getCatchTime() != null)
                .collect(Collectors.groupingBy(CatchEntry::getTimeOfDay));

        for (Map.Entry<String, List<CatchEntry>> entry : byTime.entrySet()) {
            List<CatchEntry> timeCatches = entry.getValue();
            int count = timeCatches.size();
            double avgWeight = timeCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .average().orElse(0);
            double avgLength = timeCatches.stream()
                    .mapToDouble(CatchEntry::getBassLengthInches)
                    .average().orElse(0);
            double maxWeight = timeCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .max().orElse(0);

            stats.put(entry.getKey(), new TimeStats(count, avgWeight, avgLength, maxWeight));
        }

        return stats;
    }

    /**
     * Get weather analysis.
     */
    public Map<String, WeatherStats> getWeatherAnalysis() {
        List<CatchEntry> catches = catchDAO.getAllCatches();
        Map<String, WeatherStats> stats = new LinkedHashMap<>();

        Map<String, List<CatchEntry>> byWeather = catches.stream()
                .filter(c -> c.getWeather() != null && !c.getWeather().isEmpty())
                .collect(Collectors.groupingBy(CatchEntry::getWeather));

        for (Map.Entry<String, List<CatchEntry>> entry : byWeather.entrySet()) {
            List<CatchEntry> weatherCatches = entry.getValue();
            int count = weatherCatches.size();
            double avgWeight = weatherCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .average().orElse(0);
            double avgTemp = weatherCatches.stream()
                    .mapToInt(CatchEntry::getTemperatureF)
                    .average().orElse(0);
            double maxWeight = weatherCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .max().orElse(0);

            stats.put(entry.getKey(), new WeatherStats(count, avgWeight, avgTemp, maxWeight));
        }

        return stats.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().count, a.getValue().count))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Get location comparison stats.
     */
    public Map<String, LocationStats> getLocationAnalysis() {
        List<CatchEntry> catches = catchDAO.getAllCatches();
        Map<String, LocationStats> stats = new LinkedHashMap<>();

        Map<String, List<CatchEntry>> byLocation = catches.stream()
                .filter(c -> c.getLocation() != null && !c.getLocation().isEmpty())
                .collect(Collectors.groupingBy(CatchEntry::getLocation));

        for (Map.Entry<String, List<CatchEntry>> entry : byLocation.entrySet()) {
            List<CatchEntry> locCatches = entry.getValue();
            int count = locCatches.size();
            double avgWeight = locCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .average().orElse(0);
            double avgLength = locCatches.stream()
                    .mapToDouble(CatchEntry::getBassLengthInches)
                    .average().orElse(0);
            double maxWeight = locCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .max().orElse(0);
            double totalWeight = locCatches.stream()
                    .mapToDouble(CatchEntry::getBassWeightLbs)
                    .sum();

            stats.put(entry.getKey(), new LocationStats(count, avgWeight, avgLength, maxWeight, totalWeight));
        }

        return stats.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().count, a.getValue().count))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Get overall summary statistics.
     */
    public SummaryStats getSummaryStats() {
        List<CatchEntry> catches = catchDAO.getAllCatches();

        if (catches.isEmpty()) {
            return new SummaryStats(0, 0, 0, 0, 0, null, null, null);
        }

        int totalCatches = catches.size();
        double totalWeight = catches.stream().mapToDouble(CatchEntry::getBassWeightLbs).sum();
        double avgWeight = totalWeight / totalCatches;
        double avgLength = catches.stream().mapToDouble(CatchEntry::getBassLengthInches).average().orElse(0);

        CatchEntry biggest = catches.stream()
                .max(Comparator.comparingDouble(CatchEntry::getBassWeightLbs))
                .orElse(null);

        double biggestWeight = biggest != null ? biggest.getBassWeightLbs() : 0;

        // Find most productive bait
        Map<String, Long> baitCounts = catches.stream()
                .filter(c -> c.getBait() != null)
                .collect(Collectors.groupingBy(CatchEntry::getBait, Collectors.counting()));
        String topBait = baitCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Find most productive location
        Map<String, Long> locCounts = catches.stream()
                .filter(c -> c.getLocation() != null)
                .collect(Collectors.groupingBy(CatchEntry::getLocation, Collectors.counting()));
        String topLocation = locCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Find best time of day
        Map<String, Long> timeCounts = catches.stream()
                .filter(c -> c.getCatchTime() != null)
                .collect(Collectors.groupingBy(CatchEntry::getTimeOfDay, Collectors.counting()));
        String bestTime = timeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new SummaryStats(totalCatches, totalWeight, avgWeight, avgLength, biggestWeight,
                topBait, topLocation, bestTime);
    }

    // ==================== Stats Classes ====================

    public static class BaitStats {
        public final int count;
        public final double avgWeight;
        public final double avgLength;
        public final double maxWeight;

        public BaitStats(int count, double avgWeight, double avgLength, double maxWeight) {
            this.count = count;
            this.avgWeight = avgWeight;
            this.avgLength = avgLength;
            this.maxWeight = maxWeight;
        }
    }

    public static class TimeStats {
        public final int count;
        public final double avgWeight;
        public final double avgLength;
        public final double maxWeight;

        public TimeStats(int count, double avgWeight, double avgLength, double maxWeight) {
            this.count = count;
            this.avgWeight = avgWeight;
            this.avgLength = avgLength;
            this.maxWeight = maxWeight;
        }
    }

    public static class WeatherStats {
        public final int count;
        public final double avgWeight;
        public final double avgTemp;
        public final double maxWeight;

        public WeatherStats(int count, double avgWeight, double avgTemp, double maxWeight) {
            this.count = count;
            this.avgWeight = avgWeight;
            this.avgTemp = avgTemp;
            this.maxWeight = maxWeight;
        }
    }

    public static class LocationStats {
        public final int count;
        public final double avgWeight;
        public final double avgLength;
        public final double maxWeight;
        public final double totalWeight;

        public LocationStats(int count, double avgWeight, double avgLength, double maxWeight, double totalWeight) {
            this.count = count;
            this.avgWeight = avgWeight;
            this.avgLength = avgLength;
            this.maxWeight = maxWeight;
            this.totalWeight = totalWeight;
        }
    }

    public static class SummaryStats {
        public final int totalCatches;
        public final double totalWeight;
        public final double avgWeight;
        public final double avgLength;
        public final double biggestWeight;
        public final String topBait;
        public final String topLocation;
        public final String bestTime;

        public SummaryStats(int totalCatches, double totalWeight, double avgWeight, double avgLength,
                           double biggestWeight, String topBait, String topLocation, String bestTime) {
            this.totalCatches = totalCatches;
            this.totalWeight = totalWeight;
            this.avgWeight = avgWeight;
            this.avgLength = avgLength;
            this.biggestWeight = biggestWeight;
            this.topBait = topBait;
            this.topLocation = topLocation;
            this.bestTime = bestTime;
        }
    }
}
