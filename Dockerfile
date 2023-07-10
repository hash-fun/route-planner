FROM gradle:7.6-jdk AS builder

COPY ./gradle.properties ./
COPY ./*.kts ./
COPY ./src ./src

RUN gradle bootJar -i -x test -Pvaadin.productionMode=true

FROM openjdk:17-jdk AS app
WORKDIR /app

COPY --from=builder /home/gradle/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
