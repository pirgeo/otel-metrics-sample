package com.pirgeo.example.server.controller;

import com.pirgeo.example.server.temperatureprovider.OpenMeteoTemperatureProvider;
import com.pirgeo.example.server.util.Location;
import com.pirgeo.example.server.temperatureprovider.AbstractTemperatureProvider;
import com.pirgeo.example.server.temperatureprovider.SimpleTemperatureProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {
    private final AbstractTemperatureProvider temperatureProvider;

    public TemperatureController(Location location) {
        temperatureProvider = new OpenMeteoTemperatureProvider(location);
//        temperatureProvider = new SimpleTemperatureProvider();
    }

    /**
     * GET endpoint for retrieving the current temperature
     * @return A 200 OK response containing the current temperature.
     */
    @GetMapping("/temp")
    public ResponseEntity<Double> temp() {
        return ResponseEntity.ok(temperatureProvider.getTemperature());
    }
}
