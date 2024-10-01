package com.pirgeo.example.server.util;

/**
 * Helper class, describing a location (in this example, usually a city)
 */
public class Location {
    private final String name;
    private final double latitude;
    private final double longitude;

    public Location(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static final Location VIENNA = new Location("Vienna", 48.2085, 16.3721);
    public static final Location SYDNEY = new Location("Sydney", -33.8678, 151.2073);
    public static final Location NEW_YORK = new Location("New York", 40.7143, -74.006);
    public static final Location LINZ = new Location("Linz", 48.3064, 14.2861);
}
