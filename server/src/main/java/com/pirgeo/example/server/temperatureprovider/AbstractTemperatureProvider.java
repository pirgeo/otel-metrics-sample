package com.pirgeo.example.server.temperatureprovider;

import java.time.Duration;

/**
 * The abstract temperature provider implements the caching of temperature between queries
 * The implementation only needs to specify the timeBetweenUpdates in the constructor
 * and implement the actual polling of the new value.
 */
public abstract class AbstractTemperatureProvider {
    private long lastUpdatedTimestamp;
    private final long timeBetweenUpdates;
    private double temperature;

    AbstractTemperatureProvider(Duration timeBetweenUpdates) {
        this.timeBetweenUpdates = timeBetweenUpdates.toMillis();
        lastUpdatedTimestamp = 0;
    }

    /**
     * Returns the current temperature.
     * The temperature will be polled from a backend.
     * Temperatures are cached and not polled on every call to getTemperature.
     * The time between backend requests depends on the used implementation.
     * @return The current temperature
     */
    public double getTemperature() {
        if (shouldUpdateTemperature()) {
            double newTemperature = pollTemperature();
            if (!Double.isNaN(newTemperature)) {
                temperature = newTemperature;
                setLastUpdatedToNow();
            }
        }
        return temperature;
    }

    private boolean shouldUpdateTemperature() {
        return System.currentTimeMillis() - lastUpdatedTimestamp > timeBetweenUpdates;
    }

    private void setLastUpdatedToNow() {
        lastUpdatedTimestamp = System.currentTimeMillis();
    }

    abstract double pollTemperature();
}
