# statistic-analyzer project

This project uses Quarkus, the Supersonic Subatomic Java Framework and GraalVM.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

If you want to learn more about GraalVM, please visit its website: https://www.graalvm.org/ .

This project uses [Mongodb Client](https://quarkus.io/guides/mongodb)

## Running Mongodb Docker
```shell
docker run -ti --rm -p 27017:27017 mongo:4.0
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -P dev
```
