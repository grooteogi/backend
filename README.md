Grooteogi Backend
===

🚀 Getting Started
---

```bash
docker-compose -f docker-compose.local.yml up
```

🧐 How to develop
---

```bash
# Execute docker-compose to run development database
docker-compose -f docker-compose.dev.yml up

# Run development api server
./gradlew build
java -jar ./build/libs/[build_file].jar --spring.profiles.activate=dev
```

⚙️ Profiles
---

### local

This profile for testing communication with the frontend in local.

- docker configuration file
  - docker-compose.local.yml
- spring configuration file
  - application.local.yml

### develop

This profile for development in local.

- docker configuration file
  - docker-compose.dev.yml
- spring configuration file
  - application.dev.yml

### production

This profile for deployment in server.

- spring configuration file
  - application.prod.yml
