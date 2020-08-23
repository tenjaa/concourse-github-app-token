FROM oracle/graalvm-ce:20.2.0-java11@sha256:099235909a9ba9e76bd81ef56a22d70674ab6d266b4c5d7c202ee6a1e1af4398 as build
RUN gu install native-image
COPY build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -H:+StaticExecutableWithDynamicLibC -jar concourse-github-app-token.jar

FROM gcr.io/distroless/base-debian10@sha256:8756a25c4c5e902c4fe20322cc69d510a0517b51eab630c614efbd612ed568bf
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
ENTRYPOINT ["sh"]
