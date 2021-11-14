FROM openjdk:17.0.1@sha256:c7fffc2024948e6d75922025a17b7d81cb747fd0fe0167fef13c6fcfc72e4144 as java-builder
WORKDIR concourse-github-app-token
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/graalvm-ce:21.3.0@sha256:f52cef93e0c56ba9e38f9a3ca9c6e13a696aee2c3cfdc427bca235e80ac22e5c as build
RUN gu install native-image

# https://www.graalvm.org/reference-manual/native-image/StaticImages/
RUN mkdir musl
RUN curl http://musl.cc/x86_64-linux-musl-native.tgz --output musl.tgz
RUN tar -xzf musl.tgz -C musl --strip-components 1
RUN mkdir zlib
RUN curl https://zlib.net/zlib-1.2.11.tar.gz --output zlib.tar.gz
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

FROM alpine:3.14.2@sha256:e1c082e3d3c45cccac829840a25941e679c25d438cc8412c2fa221cf1a824e6a
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
