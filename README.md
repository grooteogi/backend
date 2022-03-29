Grooteogi Backend
===

Getting Start
---

### Execute docker-compose to run development database

```bash
docker-compose -f docker-compose.dev.yml up
```

### Run development api server

```bash
./gradlew build # build spring-boot project
java -jar ./build/libs/[build_file].jar --spring.profiles.activate=dev
```

How to develop
---

### Controller

[Controller](/api/src/main/java/grooteogi/controller) is implementation of the `api`. This class communicates directly with the user.

> Business logic must not be included in the source code.

### Service

[Service](/api/src/main/java/grooteogi/service) is a class that handles requests from controllers. `All business logic` is contained here.

### Domain

[Domain](/api/src/main/java/grooteogi/domain) is a class that matches a table stored in the DB. The DB and connection are already established, so if you need additional tables, you just need to create a domain object.

### Repository

[Repository](/api/src/main/java/grooteogi/repository) is a interface that allows you to work with DB. It can be created by inheriting from `JpaRepository`.

### Utils

[Utils](/api/src/main/java/grooteogi/utils) is a useful class package needed during development.

> A typical example is [RedisClient](api/src/main/java/grooteogi/utils/RedisClient.java). To set and get data from redis, you can use the `setValue` and `getValue` function of that class.
