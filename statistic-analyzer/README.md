# statistic-analyzer project

This project uses Quarkus, the Supersonic Subatomic Java Framework and GraalVM.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

If you want to learn more about GraalVM, please visit its website: https://www.graalvm.org/ .

This project uses [Mongodb Client](https://quarkus.io/guides/mongodb)
This project uses [OpenApi - SwaggerUi](https://quarkus.io/guides/openapi-swaggerui)

## Running Mongodb Docker
```shell
docker run -ti --rm -p 27017:27017 mongo:4.0
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -P dev
```

## Statistic Analyzer

This module is used to analyze all api call thougth the core proxy module.

The core module send a http async call with the following information.

These properties are used by the `statistic-analyzer`:

| Field Named       | Description                                       | Example               | Type    
|-------------------|---------------------------------------------------|-----------------------|--------|
| `basePath`        | BasePath of the backend API                       | `oauth/token`         | String    
| `contentLength`   | Response's body contentLength                     | `167`                 | int       
| `responseTime`    | Amount of time the core module need to process    | `352`                 | int       
| `responseCode`    | Response code from backend                        | `200`                 | int       
| `appId`           | AppId from Authentication Header                  | `2489712506316422`    | String    
| `userId`          | UserId  from Authentication Header                | `683916458`           | String    

## SwaggerUI

Once your application is started, you can make a request to the default /q/openapi endpoint to get schema
and /q/swagger-ui to access Swageger UI Page