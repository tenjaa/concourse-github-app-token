FROM oracle/graalvm-ce:20.1.0-java11 as build
RUN gu install native-image
COPY build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM debian:10.4-slim
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
ENTRYPOINT ["sh"]
