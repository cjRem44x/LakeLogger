package com.lakelogger.model;

/**
 * Represents a fishing bait type.
 */
public class Bait {
    private int id;
    private String name;
    private String type; // Soft plastic, Crankbait, Jig, Topwater, etc.

    public Bait() {
    }

    public Bait(String name) {
        this.name = name;
    }

    public Bait(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bait bait = (Bait) obj;
        return name != null && name.equals(bait.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
