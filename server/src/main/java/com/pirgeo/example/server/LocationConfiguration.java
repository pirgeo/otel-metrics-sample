package com.pirgeo.example.server;

import com.pirgeo.example.server.util.Location;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the {@link Location}.
 * If the config property `coffeeshop.location` is set, this class will attempt to convert it to a
 * bean of type {@link Location}. If it fails, it will fall back to a default location.
 */
@Configuration
@ConfigurationProperties(prefix = "coffeeshop")
public class LocationConfiguration {
    private static final Location DEFAULT_LOCATION = Location.VIENNA;

    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Bean
    Location location() {
        // if unset, fall back to default
        if (getLocation() == null) {
            return DEFAULT_LOCATION;
        }

        return switch (getLocation().replaceAll("\s", "").toLowerCase()) {
            case "vienna" -> Location.VIENNA;
            case "newyork" -> Location.NEW_YORK;
            case "sydney" -> Location.SYDNEY;
            case "linz" -> Location.LINZ;
            default -> DEFAULT_LOCATION;
        };
    }

}
