FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Ensure the JAR name matches your actual build output
COPY target/*.jar online_exam_services.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "online_exam_services.jar"]
