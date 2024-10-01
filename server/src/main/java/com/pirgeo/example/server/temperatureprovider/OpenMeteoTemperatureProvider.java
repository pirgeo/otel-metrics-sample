package com.pirgeo.example.server.temperatureprovider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirgeo.example.server.util.Location;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * An implementation of the {@link AbstractTemperatureProvider} talking to the OpenMeteo API
 * and retrieving the current temperature for a specific location.
 * This provider will only call the API every few minutes, not every time the getTemperature method is called.
 */
public class OpenMeteoTemperatureProvider extends AbstractTemperatureProvider {
    private static final String apiUrlTemplate = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m";
    private final URL apiUrl;
    private final ObjectMapper objectMapper;

    private static final Logger logger = Logger.getLogger(OpenMeteoTemperatureProvider.class.getName());

    public OpenMeteoTemperatureProvider(Location location) {
        // only poll the API every 5 minutes
        super(Duration.ofMinutes(5));

        DecimalFormat df = new DecimalFormat("###.##", new DecimalFormatSymbols(Locale.ROOT));
        try {
            apiUrl = new URL(String.format(apiUrlTemplate, df.format(location.getLatitude()), df.format(location.getLongitude())));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        objectMapper = new ObjectMapper();
    }

    @Override
    double pollTemperature() {
        try {
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                OpenMeteoResponseObject openMeteoResponseObject = objectMapper.readValue(connection.getInputStream(), OpenMeteoResponseObject.class);
                if (openMeteoResponseObject != null) {
                    logger.info("got temperature from OpenMeteo: " + openMeteoResponseObject.getCurrent().getTemperature_2m());
                    return openMeteoResponseObject.getCurrent().getTemperature_2m();
                }
            }
        } catch (IOException e) {
            logger.warning("failed to read temperature from OpenMeteo: " + e.getMessage());
        }
        return Double.NaN;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenMeteoResponseObject {
        private Current current;

        public Current getCurrent() {
            return current;
        }

        public void setCurrent(Current current) {
            this.current = current;
        }

        public OpenMeteoResponseObject() {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Current {
            private double temperature_2m;
            private String time;

            public double getTemperature_2m() {
                return temperature_2m;
            }

            public void setTemperature_2m(double temperature_2m) {
                this.temperature_2m = temperature_2m;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public Current() {
            }
        }
    }
}
