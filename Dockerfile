FROM oracle/graalvm-ce:20.2.0-java11@sha256:099235909a9ba9e76bd81ef56a22d70674ab6d266b4c5d7c202ee6a1e1af4398 as build
RUN gu install native-image
COPY build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM debian:10.5-slim@sha256:8d81110c3f93a777e3f4053a6b18b70e4a1003655b8c2664bdf18b19043f99d9
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
ENTRYPOINT ["sh"]
