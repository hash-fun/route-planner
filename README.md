Система управления логистикой
=============================

## Для сборки потребуются

1) [JDK 21](https://jdk.java.net/21/) или альтернативы
2) [Docker](https://www.docker.com/) (не обязательно) 

## Ключи Google API

для корректного отображения карт необходимо указать валидный Google API Key в [application.properties](src/main/resources/application-h2.properties)
```properties
app.google.api-key=AIza...
```

## Сборка

```shell
./gradlew clean build -x test
```

## Запуск

```shell
./gradlew bootRun
```

## Если собирать лень

Запустите контейнер

```shell
docker run -p 8080:8080 sfedu/geo
```

## Конфигурирование

1) Сервис использует H2 в режиме in-memory
2) В файле [application-h2.properties](src/main/resources/application-h2.properties) вы сможете найти пример как запустить сервер с сохранением на диск
3) При желании запустить на Postgres необходимо добавить драйвер в [build.gradle.kts](build.gradle.kts) и указать `spring.datasource.url` в application.properties.


