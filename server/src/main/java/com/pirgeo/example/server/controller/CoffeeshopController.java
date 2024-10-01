package com.pirgeo.example.server.controller;

import com.pirgeo.example.server.util.CoffeeMachine;
import com.pirgeo.example.server.util.Location;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

import static com.pirgeo.example.server.util.CoffeeMachine.BeverageType.*;

/**
 * The main controller for the application
 * Allows entering and leaving the coffee shop, and ordering beverages.
 */
@RestController
public class CoffeeshopController {
    private final CoffeeMachine coffeeMachine;

    private static final String BEVERAGE_TYPE_KEY = "beverageType";
    private final Location location;

    private final LongUpDownCounter customersInStore;
    private final LongCounter beveragesOrdered;
    private final LongHistogram brewTimes;

    private final Attributes locationAttributes;

    public CoffeeshopController(Location location, OpenTelemetry openTelemetry) {
        this.location = location;
        // turn on the coffee machine
        coffeeMachine = new CoffeeMachine();

        locationAttributes = Attributes.builder().put("location", location.getName()).build();

        Meter meter = openTelemetry.getMeter("com.pirgeo.example.coffeeshop");
        customersInStore = meter.upDownCounterBuilder("coffeeshop.customers_in_store").build();
        brewTimes = meter.histogramBuilder("coffeeshop.brew_time").ofLongs().setUnit("ms").build();
        beveragesOrdered = meter.counterBuilder("coffeeshop.beverages.ordered").build();
    }

    @GetMapping("/enter")
    public ResponseEntity<String> enter() {
        customersInStore.add(1, locationAttributes);
        return ResponseEntity.ok("Welcome to the Coffee shop!");
    }

    @GetMapping("/leave")
    public ResponseEntity<String> leave() {
        customersInStore.add(-1, locationAttributes);
        return ResponseEntity.ok("Thank you for visiting the Coffee shop! We hope to see you again soon!");
    }

    @PostMapping("/order")
    public ResponseEntity<String> orderCoffee(@RequestBody Map<String, String> requestBody) {
        if (!requestBody.containsKey(BEVERAGE_TYPE_KEY)) {
            return ResponseEntity.badRequest().body("'" + BEVERAGE_TYPE_KEY + "' must be set.");
        }

        CoffeeMachine.BeverageType beverageType = parse(requestBody.get(BEVERAGE_TYPE_KEY));
        if (beverageType == null) {
            return ResponseEntity.badRequest().body("Beverage type '" + requestBody.get(BEVERAGE_TYPE_KEY) + "' is not valid.");
        }
        Attributes beverageAttributes = Attributes.builder().put("type", beverageType.toString()).putAll(locationAttributes).build();
        beveragesOrdered.add(1, beverageAttributes);

        Duration brewTime = coffeeMachine.prepareBeverage(beverageType);
        brewTimes.record(brewTime.toMillis(), beverageAttributes);

        return ResponseEntity.ok(String.valueOf(brewTime.toMillis()));
    }
}
