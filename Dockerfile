FROM amazoncorretto:17

WORKDIR /app

COPY target/walletapp-0.0.1-SNAPSHOT.jar /app/walletapp.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/walletapp.jar"]