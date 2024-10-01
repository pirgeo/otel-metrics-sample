package com.pirgeo.example.server.controller;

import com.pirgeo.example.server.temperatureprovider.OpenMeteoTemperatureProvider;
import com.pirgeo.example.server.util.Location;
import com.pirgeo.example.server.temperatureprovider.AbstractTemperatureProvider;
import com.pirgeo.example.server.temperatureprovider.SimpleTemperatureProvider;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {
    private final AbstractTemperatureProvider temperatureProvider;

    public TemperatureController(Location location, OpenTelemetry openTelemetry) {
        temperatureProvider = new OpenMeteoTemperatureProvider(location);
//        temperatureProvider = new SimpleTemperatureProvider();

        Meter meter = openTelemetry.getMeter("temperature_contoller");
        Attributes attributes = Attributes.builder().put("location", location.getName()).build();
        meter.gaugeBuilder("coffeeshop.outside_temperature")
                .buildWithCallback(gauge -> gauge.record(temperatureProvider.getTemperature(), attributes));
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
