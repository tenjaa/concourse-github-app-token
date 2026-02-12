FROM ghcr.io/graalvm/jdk-community:21@sha256:57e5c4cf8536dac4346611691c31ea4828f3ff7bbb0d3695902b234f69ed99b5 as java-builder
RUN microdnf install findutils
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/native-image-community:21@sha256:faed0fd6809b138254bdd6c7046e56894f4d9566ecbc7b0952aab43e65e16e0e  as build
COPY --from=java-builder app/build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM debian:12-slim@sha256:98f4b71de414932439ac6ac690d7060df1f27161073c5036a7553723881bffbe
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
