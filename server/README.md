# Server application 

This is the server application producing OTel metrics. 
It offers 4 endpoints:

- `/temp`: returns the current temperature in degrees Celsius (double)
- `/enter`: enter the coffee shop
- `/leave`: leave the coffee shop
- `/order`: order a beverage at the store. Accepts a JSON with a key called `beverageType`. Can be one of `babyccino`, `cappuccino`, `espresso`, `tea`.

## Running the application

The coffee shop can be opened in different locations.
Currently supported locations are: `Vienna`, `Linz`, `Sydney`, `New York`
In order to run the coffee shop in a certain location, you can use the [application.yaml](src/main/resources/application.yaml) file.
To set the location, simply set the property `coffeeshop.location` to one of the above values.

For simplicity (and to be able to run multiple instances on the same host without ports colliding) we provide profile files for individual cities in this repository. 
To run the app with a profile, run (replace `sydney` with whatever city you want your coffee shop to be in, same list as above applies): 

```shell
./gradlew :server:bootRun --args='--spring.profiles.active=sydney'
```

### Exporting directly to a backend

By default, the OTLP data is exported to the local OTel collector endpoint.
This can be changed directly in the application properties.
To export to Dynatrace, you could set: 

```yaml
  exporter:
    otlp:
      metrics:
        temporality:
          preference: DELTA
      endpoint: "https://{your-environment-id}.live.dynatrace.com/api/v2/otlp"
      headers:
        "Authorization": "Api-Token dt0c01.<your>.<token>"
```
