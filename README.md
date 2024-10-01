# OpenTelemetry Metrics sample with Spring Boot

This repository contains an example app to showcase OpenTelemetry metrics in a Spring Boot app.
It is intended to be used in talks that show how OpenTelemetry metrics work.

The repository contains two applications: 
- [A server](server) that represents the coffee shop
- [A client](client) that represents customers shopping at said coffee shop

## The coffee shop

The server application is a simple app that offers a few endpoints: 
- `/temp`: An endpoint that returns the current outside temperature for the coffee shop's location.
- `/enter`: An endpoint indicating that a customer entered the coffee shop.
- `/leave`: An endpoint indicating that a customer has left the shop.
- `/order`: A POST endpoint that can be used to order a beverage. It expects a JSON body containing a `beverageType` key.

The coffee also has a location.
Currently available locations are `Vienna`, `Linz`, `New York` and `Sydney`.
The application will call the [OpenMeteo](https://open-meteo.com/) API to retrieve the current temperature at the location.

## The client app

The client app represents multiple customers.
Customers will:
- Read the outside temperature (`GET /temp`)
- Enter the coffee shop (`GET /enter`)
- Decide what to order
  - If the temperature is below 16 degrees C, customers are much more likely to order tea.
- Order, and wait for the beverage to be finished (`POST /order`)
- Leave the coffee shop (`GET /leave`)

## Telemetry

In order to showcase the different OTel metric types, the application records some metrics:
- Gauge (`coffeeshop.outside_temperature`): The current outside temperature, split by location.
- Counter (`coffeeshop.beverages.ordered`): The number of ordered beverages, split by location and beverage.
- UpDownCounter (`coffeeshop.customers_in_store`): The number of customers currently in the store. `/enter` will increase this value, `/leave` will decrease it. Split by location.
- Histogram (`coffeeshop.brew_time`): A histogram of beverage brew times. Split by location and drink.

## Further reading
- [OTel docs](https://opentelemetry.io/docs/)