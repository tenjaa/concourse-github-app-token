FROM openjdk:21 as java-builder
RUN microdnf install findutils
WORKDIR concourse-github-app-token
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/graalvm-ce:22.3.1@sha256:6c937801101719e7d994f1eb534a9a91d315fe0494cabb664028cc4d9f8adbf5  as build
RUN gu install native-image

# https://www.graalvm.org/reference-manual/native-image/StaticImages/
WORKDIR /
RUN mkdir musl
# https://more.musl.cc/10.2.1/x86_64-linux-musl/x86_64-linux-musl-native.tgz
COPY lib/musl.tgz musl.tgz
RUN tar -xzf musl.tgz -C musl --strip-components 1
RUN mkdir zlib
# https://zlib.net/zlib-1.2.12.tar.gz
COPY lib/zlib.tar.gz zlib.tar.gz
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
RUN cd /app; native-image --no-fallback --static --libc=musl --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM alpine:3.17.2@sha256:ff6bdca1701f3a8a67e328815ff2346b0e4067d32ec36b7992c1fdc001dc8517
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
