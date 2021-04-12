# core project

This project uses Quarkus, the Supersonic Subatomic Java Framework and GraalVM.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

If you want to learn more about GraalVM, please visit its website: https://www.graalvm.org/ .

This project uses [Redis Client](https://quarkus.io/guides/redis)

## Running Redis docker
```shell
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name redis_quarkus_test -p 6379:6379 redis:5.0.6
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -P dev
```