FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE
ENV Docker_Env Yes
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]