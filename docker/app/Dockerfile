FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/target/walletapp-0.0.1-SNAPSHOT.jar /app/walletapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/walletapp.jar"]