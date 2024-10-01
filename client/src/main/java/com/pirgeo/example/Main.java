package com.pirgeo.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        int numThreads = 30;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        String baseUrl = System.getenv("SERVER_URL");
        if (baseUrl == null) {
            baseUrl = "http://localhost:8080";
        }

        final String finalBaseUrl = baseUrl;

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    CoffeeshopCustomer customer = new CoffeeshopCustomer(finalBaseUrl);
                    customer.getACoffee();

                    try {
                        Thread.sleep(30_000 + ThreadLocalRandom.current().nextInt(100_000));
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
    }
}