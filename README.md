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
