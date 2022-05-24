Grooteogi Backend
===

![deploy](https://img.shields.io/travis/com/grooteogi/backend/develop?label=deploy&logo=travis&style=flat-square)

🚀 Getting Started
---

1. Set up environments variables.
2. Execute docker-compose to run db server.

    ```bash
    docker-compose up
    ```

3. Run Spring Boot project in IntelliJ.

🧐 How to set env vars 
---

- In local environment, [local.env](./api/config/local.env) is used.
  - Please refer to that [link](https://github.com/grooteogi/backend/wiki/env_file)
- In server environment, use the `aws parameter store`.

⚙️ Profiles
---

> Common settings are defined in [application.yml](./api/src/main/resources/application.yml).

### local

- profile for testing locally.
- [application.local.yml](./api/src/main/resources/application-local.yml)

### dev

- profile for develop server.
- [application.dev.yml](./api/src/main/resources/application-dev.yml)

### prod

- profile for production server.
- application.prod.yml
