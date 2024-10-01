package com.pirgeo.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class CoffeeshopCustomer {
    private final String enterUrl;
    private final String leaveUrl;
    private final String orderUrl;
    private final String tempUrl;

    private final Random random = new Random();

    private static final Logger LOGGER = Logger.getLogger(CoffeeshopCustomer.class.getName());

    public CoffeeshopCustomer(String baseUrl) {
        this.enterUrl = baseUrl + "/enter";
        this.leaveUrl = baseUrl + "/leave";
        this.orderUrl = baseUrl + "/order";
        this.tempUrl = baseUrl + "/temp";
    }

    public void getACoffee() {
        long entered = System.currentTimeMillis();
        if (!enterStore()) {
            return;
        }

        orderInStore(decideWhatToOrder());
        leaveStore();
        LOGGER.info("customer was in store for " + Duration.ofMillis(System.currentTimeMillis() - entered).toSeconds() + "s");
    }

    private boolean enterStore() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(enterUrl).openConnection();
            if (connection.getResponseCode() == 200) {
                LOGGER.fine(String.format("entered the store (code %s)", connection.getResponseCode()));
                return true;
            } else {
                LOGGER.warning(String.format("failed to enter the store (code %s)", connection.getResponseCode()));
                return false;
            }
        } catch (IOException e) {
            LOGGER.warning("failed to enter the store: " + e.getMessage());
            return false;
        }
    }

    private void leaveStore() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(leaveUrl).openConnection();
            if (connection.getResponseCode() == 200) {
                LOGGER.fine(String.format("left the store (code %s)", connection.getResponseCode()));
            } else {
                LOGGER.warning(String.format("failed to leave the store (code %s)", connection.getResponseCode()));
            }
        } catch (IOException e) {
            LOGGER.warning("failed to leave the store: " + e.getMessage());
        }
    }

    private void orderInStore(String beverage) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(orderUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"beverageType\":\"" + beverage + "\"}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            }

            if (connection.getResponseCode() == 200) {
                LOGGER.info(String.format("Ordered %s (code %s)", beverage, connection.getResponseCode()));
                String response = responseBodyToString(connection);

                try {
                    int millis = Integer.parseInt(response);
                    LOGGER.fine("waiting for preparation of " + beverage + " to complete...");
                    Thread.sleep(millis);
                    LOGGER.info("waited " + millis + "ms for " + beverage);
                } catch (NumberFormatException e) {
                    LOGGER.warning(String.format("failed to parse order response: %s", response));
                } catch (InterruptedException e) {
                    LOGGER.warning("customer did not wait for the completion of the beverage and left.");
                }
            } else {
                LOGGER.warning("failed to order " + beverage + ": error code " + connection.getResponseCode());
            }
        } catch (IOException e) {
            LOGGER.warning("failed to order " + beverage + ": " + e.getMessage());
        }
    }

    private String decideWhatToOrder() {
        // customer ponders what to order
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(20_000));
        } catch (InterruptedException ignored) {
        }

        double temp = readTemperature();
        String beverage;

        if (temp < 16) {
            // if the temp is below 20 degrees, the chance of ordering tea is 60%
            if (random.nextDouble() <= 0.6) {
                beverage = "tea";
            } else {
                beverage = randomCoffee();
            }
            // if the temperature is higher, the chance of someone ordering tea drops to 20%
        } else if (random.nextDouble() <= 0.2) {
            beverage = "tea";
        } else {
            beverage = randomCoffee();
        }

        return beverage;
    }

    private double readTemperature() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(tempUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            LOGGER.fine(String.format("Got Temperature (code %s)", connection.getResponseCode()));

            return Double.parseDouble(responseBodyToString(connection));
        } catch (IOException ignored) {
        }
        return 0;
    }

    private static String responseBodyToString(HttpURLConnection connection) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        }
        return sb.toString();
    }

    private String randomCoffee() {
        if (random.nextDouble() <= 0.33) {
            return "espresso";
        } else if (random.nextDouble() <= 0.66) {
            return "babyccino";
        } else {
            return "cappuccino";
        }
    }
}
