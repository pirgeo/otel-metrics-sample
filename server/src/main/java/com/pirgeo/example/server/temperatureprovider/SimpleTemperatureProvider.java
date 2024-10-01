package com.pirgeo.example.server.temperatureprovider;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple temperature provider.
 * It randomly increases / decreases the current temperature reading, keeping it between ~12 and 20 degrees
 */
public class SimpleTemperatureProvider extends AbstractTemperatureProvider {
    private double currentTemperature;

    public SimpleTemperatureProvider() {
        super(Duration.ofSeconds(20));
        currentTemperature = 16.;
    }

    @Override
    double pollTemperature() {
        if (currentTemperature > 20) {
            // it will get colder again
            currentTemperature -= ThreadLocalRandom.current().nextDouble(3);
        } else if (currentTemperature < 12) {
            // it will get warmer again
            currentTemperature += ThreadLocalRandom.current().nextDouble(3);
        } else {
            // it will get warmer or colder
            currentTemperature += ThreadLocalRandom.current().nextDouble(-3, 3);
        }
        return currentTemperature;
    }
}
