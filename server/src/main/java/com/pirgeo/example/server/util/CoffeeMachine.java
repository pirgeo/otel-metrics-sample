package com.pirgeo.example.server.util;

import java.time.Duration;
import java.util.Random;

public class CoffeeMachine {
    private final Random random = new Random();

    public CoffeeMachine() {
    }

    public Duration prepareBeverage(BeverageType type) {
        Duration brewTime = Duration.ZERO;

        switch (type) {
            case TEA -> brewTime = dispenseHotWater();
            case BABYCCINO -> brewTime = foamMilk();
            case ESPRESSO -> brewTime = makeEspresso();
            case CAPPUCCINO -> brewTime = makeEspresso().plus(foamMilk());
        }

        return brewTime;
    }

    private Duration makeEspresso() {
        // takes around 25s
        return Duration.ofMillis(25_000 + random.nextInt(10_000));
    }

    private Duration foamMilk() {
        // takes around 50s
        return Duration.ofMillis(50_000 + random.nextInt(10_000));
    }

    private Duration dispenseHotWater() {
        // takes around 5 to 15 seconds
        return Duration.ofMillis(5_000 + random.nextInt(10_000));
    }

    public enum BeverageType {
        BABYCCINO("Babyccino"),
        CAPPUCCINO("Cappucino"),
        ESPRESSO("Espresso"),
        TEA("Tea"),
        ;

        private final String prettyName;

        BeverageType(String prettyName) {
            this.prettyName = prettyName;
        }

        @Override
        public String toString() {
            return prettyName;
        }

        public static BeverageType parse(String beverageType) {
            return switch (beverageType.toLowerCase()) {
                case "babyccino" -> BABYCCINO;
                case "cappuccino" -> CAPPUCCINO;
                case "espresso" -> ESPRESSO;
                case "tea" -> TEA;
                default -> null;
            };
        }
    }

}
