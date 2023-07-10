Система управления логистикой
=============================

## Для сборки потребуются

1) [JDK 20](https://jdk.java.net/20/) или альтернативы

## Сборка

```shell
./gradlew clean build -x test
```

## Запуск

```shell
./gradlew bootRun
```

Допущения
=========

1) Сервис использует H2 в режиме in-memory
2) В файле [application-local.properties](src%2Fmain%2Fresources%2Fapplication-local.properties) вы сожете найти пример как запустить в режиме разделяемого сервера на диске
3) При желании запустить на Postgres необходимо добавиь драйвер в [build.gradle.kts](build.gradle.kts) и указать `spring.datasource.url` в [application.properties](src%2Fmain%2Fresources%2Fapplication.properties)


