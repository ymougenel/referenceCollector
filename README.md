# referenceCollector


# Development

Run a postgres database:
```shell
docker run \
    -p 5432:5432 \
    -e POSTGRES_USER=dev \
    -e POSTGRES_PASSWORD=dev \
    -e POSTGRES_DB=reference_collector postgres:11.1

```
Compile the application:
```shell
mvn clean package
```


Run the generated jar
```shell
java -Dspring.profiles.active=dev -jar target/referenceCollector-0.0.1-SNAPSHOT.jar
```
