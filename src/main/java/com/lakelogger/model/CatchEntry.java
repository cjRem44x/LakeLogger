package com.lakelogger.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Represents a bass catch entry in the fishing log.
 */
public class CatchEntry {
    private int id;
    private LocalDate catchDate;
    private LocalTime catchTime;
    private double bassLengthInches;
    private double bassWeightLbs;
    private String location;
    private String weather;
    private int temperatureF;
    private String bait;
    private String notes;
    private LocalDateTime createdAt;

    public CatchEntry() {
        this.catchDate = LocalDate.now();
        this.catchTime = LocalTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public CatchEntry(LocalDate catchDate, LocalTime catchTime, double bassLengthInches,
                      double bassWeightLbs, String location, String weather,
                      int temperatureF, String bait, String notes) {
        this.catchDate = catchDate;
        this.catchTime = catchTime;
        this.bassLengthInches = bassLengthInches;
        this.bassWeightLbs = bassWeightLbs;
        this.location = location;
        this.weather = weather;
        this.temperatureF = temperatureF;
        this.bait = bait;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getCatchDate() {
        return catchDate;
    }

    public void setCatchDate(LocalDate catchDate) {
        this.catchDate = catchDate;
    }

    public LocalTime getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(LocalTime catchTime) {
        this.catchTime = catchTime;
    }

    public double getBassLengthInches() {
        return bassLengthInches;
    }

    public void setBassLengthInches(double bassLengthInches) {
        this.bassLengthInches = bassLengthInches;
    }

    public double getBassWeightLbs() {
        return bassWeightLbs;
    }

    public void setBassWeightLbs(double bassWeightLbs) {
        this.bassWeightLbs = bassWeightLbs;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(int temperatureF) {
        this.temperatureF = temperatureF;
    }

    public String getBait() {
        return bait;
    }

    public void setBait(String bait) {
        this.bait = bait;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the time of day category for analytics.
     */
    public String getTimeOfDay() {
        if (catchTime == null) return "Unknown";
        int hour = catchTime.getHour();
        if (hour >= 5 && hour < 12) return "Morning";
        if (hour >= 12 && hour < 17) return "Midday";
        if (hour >= 17 && hour < 21) return "Evening";
        return "Night";
    }

    @Override
    public String toString() {
        return String.format("CatchEntry[id=%d, date=%s, weight=%.2f lbs, length=%.1f in, location=%s]",
                id, catchDate, bassWeightLbs, bassLengthInches, location);
    }
}
