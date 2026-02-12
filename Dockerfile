FROM ghcr.io/graalvm/jdk-community:21 as java-builder
RUN microdnf install findutils
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/native-image-community:21  as build
COPY --from=java-builder app/build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM debian:12-slim
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
