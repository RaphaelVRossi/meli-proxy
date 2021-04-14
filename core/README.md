# core project

This project uses Quarkus, the Supersonic Subatomic Java Framework and GraalVM.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

If you want to learn more about GraalVM, please visit its website: https://www.graalvm.org/ .

All api are redirect to [Mercado Livre API](https://developers.mercadolibre.com.ar/pt_br)

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

## Proxy Configuration

This proxy can set a maximum calls per api, based on the followings properties:

| Property Name                             | Description                       | Required  | Type      | Default   | Values
|-------------------------------------------|-----------------------------------|-----------|-----------|-----------|--------------------|
| `proxy.http.quota.ip.enable`              | Enable Quota by Calling IP        | `false`   | Boolean   | true      | `true, false`
| `proxy.http.quota.ip.max-calls`           | Max calls per Calling IP          | `false`   | Integer   | 15        | `Integer.MAX_VALUE`
| `proxy.http.quota.path.enable`            | Enable Quota by Path (URI)        | `false`   | Boolean   | true      | `true, false`
| `proxy.http.quota.path.max-calls`         | Max calls per Path                | `false`   | Integer   | 15        | `Integer.MAX_VALUE`
| `proxy.http.quota.ip-path.enable`         | Enable Quota by Calling Ip + Path | `false`   | Boolean   | true      | `true, false`
| `proxy.http.quota.ip-path.max-calls`      | Max calls per Calling Ip + Path   | `false`   | Integer   | 15        | `Integer.MAX_VALUE`
| `proxy.http.quota.path-appid.enable`      | Enable Quota by Path + AppId      | `false`   | Boolean   | true      | `true, false`
| `proxy.http.quota.path-appid.max-calls`   | Max calls per Path + AppId        | `false`   | Integer   | 15        | `Integer.MAX_VALUE`
| `proxy.http.quota.path-userid.enable`     | Enable Quota by Path + UserId     | `false`   | Boolean   | true      | `true, false`
| `proxy.http.quota.path-userid.max-calls`  | Max calls per Path + UserId       | `false`   | Integer   | 15        | `Integer.MAX_VALUE`

These properties are used by the `statistic-analyzer`:

| Property Name                             | Description                           | Required  | Type      | Default                                   | Values
|-------------------------------------------|---------------------------------------|-----------|-----------|-------------------------------------------|--------------|
| `proxy.http.statistic.enable`             | Enable statistic collector per call   | `false`   | Boolean   | true                                      | `true, false`
| `proxy.http.statistic.url`                | URL for the statistic service         | `false`   | String    | http://statistic-analyzer:8080/api-info   | -

