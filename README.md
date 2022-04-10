Grooteogi Backend
===

🚀 Getting Started
---

1. Set env_file in `./api/config/dev.env`
Please refer to that [link](https://github.com/grooteogi/backend/wiki/env_file).
2. Execute docker-compose
    ```bash
    docker-compose -f docker-compose.local.yml up
    ```

🧐 How to develop
---

1. Set env_file in `./api/config/dev.env`
Please refer to that [link](https://github.com/grooteogi/backend/wiki/env_file).
2. Execute docker-compose to run development database
    ```bash
    docker-compose -f docker-compose.dev.yml up
    ```
3. Run the Spring Boot project in IntelliJ.

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
