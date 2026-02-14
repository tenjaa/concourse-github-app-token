FROM ghcr.io/graalvm/jdk-community:25@sha256:a3bdba6a313cddc89f7e1b2649140e898c68ba356994a9c317b96b01bcb9f982 AS java-builder
RUN microdnf install findutils
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/native-image-community:21@sha256:faed0fd6809b138254bdd6c7046e56894f4d9566ecbc7b0952aab43e65e16e0e  AS build
COPY --from=java-builder app/build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM debian:13-slim@sha256:f6e2cfac5cf956ea044b4bd75e6397b4372ad88fe00908045e9a0d21712ae3ba
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
