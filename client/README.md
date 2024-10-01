# Client application

This client application simulates customers going to the coffee shop and ordering beverages. 

By default, the client will assume the server to listen on `http://localhost:8080`.

```shell
./gradlew :client:run
```

To change the server base address, set the env var `SERVER_URL`.
You might want to do that if you run multiple instances on the same machine (and the ports would otherwise collide).

```shell
SERVER_URL=http://localhost:8082 ./gradlew :client:run
```
