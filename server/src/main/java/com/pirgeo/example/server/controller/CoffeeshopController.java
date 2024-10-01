package com.pirgeo.example.server.controller;

import com.pirgeo.example.server.util.CoffeeMachine;
import com.pirgeo.example.server.util.Location;
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

    public CoffeeshopController(Location location) {
        this.location = location;
        // turn on the coffee machine
        coffeeMachine = new CoffeeMachine();
    }

    @GetMapping("/enter")
    public ResponseEntity<String> enter() {
        return ResponseEntity.ok("Welcome to the Coffee shop!");
    }

    @GetMapping("/leave")
    public ResponseEntity<String> leave() {
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

        Duration brewTime = coffeeMachine.prepareBeverage(beverageType);

        return ResponseEntity.ok(String.valueOf(brewTime.toMillis()));
    }
}
