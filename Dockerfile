FROM oracle/graalvm-ce:20.1.0-java11@sha256:b2878190f3f802b71aec3d780d361781aaf00b168148af729b3f348326fb556e as build
RUN gu install native-image
COPY build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM debian:10.5-slim@sha256:e0a33348ac8cace6b4294885e6e0bb57ecdfe4b6e415f1a7f4c5da5fe3116e02
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
ENTRYPOINT ["sh"]
