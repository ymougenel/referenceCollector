# referenceCollector


# Use it

## Requirements
* docker
* docker-compose
* maven
## Launch the application
* Get the application: `git clone git@github.com:ymougenel/referenceCollector.git`
* Compile it & generate the docker image: `mvn clean package dockerfile:build`
* Run the application: `docker-compose up`
> Your application is now available: [http://localhost:8092/references](http://localhost:8092/references)

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
java -Dspring.profiles.active=dev -jar target/referenceCollector-X.X.X.jar
```
