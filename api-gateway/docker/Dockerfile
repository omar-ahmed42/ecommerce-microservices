# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY ../mvnw .
COPY ../.mvn .mvn
COPY ../pom.xml .
COPY ../src src
# cleans mvnw file by removing spaces and CR characters
RUN sed -i 's/\r$//' mvnw  

RUN mkdir -p /workspace/app/target/protoc-plugins
RUN chmod a+x /workspace/app/target/protoc-plugins

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:21-jdk
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.omarahmed42.gateway.GatewayApplication"]