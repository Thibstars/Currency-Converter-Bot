FROM maven:3.6-openjdk-17-slim AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/

RUN mvn clean -Dmaven.test.skip=true package -f pom.xml

FROM eclipse-temurin:17
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/*.jar app.jar
# Make sure to provide an environment variable `BOT_TOKEN`
ENTRYPOINT exec java -jar /app.jar $BOT_TOKEN $DBL_TOKEN