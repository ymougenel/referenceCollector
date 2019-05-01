# ReferenceCollector

<img src="./.README/logo.png" alt="logo" width="200"/>

Helps you gather, store and share references links.
# Use it

## Requirements
* docker
* docker-compose

## Launch the application
* Get the docker file: `curl https://raw.githubusercontent.com/ymougenel/referenceCollector/master/docker-compose.yml > docker-compose.yml`
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


# License

The project is under open-source [LICENSE](LICENSE), it therefore complies to all the terms related to [the open-source philosophy](https://en.wikipedia.org/wiki/The_Open_Source_Definition).