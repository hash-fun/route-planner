Система расчета маршрута доставки грузов
========================================

## Для сборки потребуются

1) [JDK 21](https://jdk.java.net/21/) или альтернатива
2) [Docker](https://www.docker.com/) (не обязательно) 

## Ключи Google API

для корректного отображения карт необходимо 

- или указать валидный Google API Key в [application.properties](src/main/resources/application-h2.properties)
    ```properties
    app.google.api-key=AIza...
    ```
- или указать его в переменной среды, см пример ниже.

### Получение Google API Key

- Непосредственно в [Google](https://developers.google.com/maps/documentation/embed/get-api-key?hl=ru) - приоритетный вариант
- У разработчика через [запрос](mailto:malevamnnyy@sfedu.ru) - запасной вариант 

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
docker run -p 8080:8080 -e app.google.api-key=<google-maps-api-key> sfedu/route-planner
```

## Конфигурирование

1) Сервис использует H2 в режиме in-memory
2) В файле [application-h2.properties](src/main/resources/application-h2.properties) вы сможете найти пример как запустить сервер с сохранением на диск
3) При желании запустить на Postgres необходимо добавить драйвер в [build.gradle.kts](build.gradle.kts) и указать `spring.datasource.url` в application.properties.
