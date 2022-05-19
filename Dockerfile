FROM openjdk:19@sha256:2e1844d74fd943a689ddc20613b269d33489d1f194d9c93de072c245f3d4bdb8 as java-builder
WORKDIR concourse-github-app-token
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/graalvm-ce:latest@sha256:5a200da297ce846b718c56619aeaf1204686587c4bc9979d37b2c4ffd10e0806  as build
RUN gu install native-image

# https://www.graalvm.org/reference-manual/native-image/StaticImages/
RUN mkdir musl
RUN curl https://more.musl.cc/10.2.1/x86_64-linux-musl/x86_64-linux-musl-native.tgz --output musl.tgz
RUN tar -xzf musl.tgz -C musl --strip-components 1
RUN mkdir zlib
RUN curl https://zlib.net/zlib-1.2.12.tar.gz --output zlib.tar.gz
RUN tar -xzf zlib.tar.gz -C zlib --strip-components 1

ENV CC=/musl/bin/gcc
ENV PATH="/musl/bin:${PATH}"

WORKDIR zlib
RUN ./configure --prefix=../musl --static
RUN make
RUN make install
WORKDIR /

COPY --from=java-builder concourse-github-app-token/build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --libc=musl --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar --allow-incomplete-classpath

FROM alpine:3.15.4@sha256:4edbd2beb5f78b1014028f4fbb99f3237d9561100b6881aabbf5acce2c4f9454
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
