FROM openjdk:17-jdk-alpine
COPY target/Dashboard-*.jar app.jar
ENV TZ="Asia/Jakarta"
EXPOSE 9015
CMD ["java", "-Xms256m", "-Xmx1024m", "-jar", "app.jar"]
