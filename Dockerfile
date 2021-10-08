FROM openjdk:11.0.12@sha256:c558394afd1f8fdd16658c56655ca2808748561980011e45bf67873d72b1ad42 as java-builder
WORKDIR concourse-github-app-token
COPY gradle gradle
COPY src src
COPY *.kts ./
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY reflect-config.json reflect-config.json
RUN ./gradlew build --no-daemon

FROM alpine:3.14.2@sha256:e1c082e3d3c45cccac829840a25941e679c25d438cc8412c2fa221cf1a824e6a as libstdcplusplus-provider
RUN apk add g++

FROM ghcr.io/graalvm/graalvm-ce:21.2.0@sha256:3f9a165b80a6592d5609ed97d37201cbf1fc69e65027b6f7f0492967fe3550e2 as build
RUN gu install native-image

# https://www.graalvm.org/reference-manual/native-image/StaticImages/
RUN mkdir musl-gcc-tmp
COPY --from=libstdcplusplus-provider '/usr/lib/libstdc++.a' /musl-gcc-tmp
## musl
ENV PATH="/musl-gcc-tmp/bin:${PATH}"
RUN curl https://musl.libc.org/releases/musl-1.2.1.tar.gz --output musl-1.2.1.tar.gz && \
    tar -xzf musl-1.2.1.tar.gz && \
    cd musl-1.2.1 && \
    ./configure --disable-shared --prefix=/musl-gcc-tmp && \
    make && \
    make install
## zlib
ENV CC=musl-gcc
RUN curl https://zlib.net/zlib-1.2.11.tar.gz --output zlib-1.2.11.tar.gz && \
    tar -xzf zlib-1.2.11.tar.gz && \
    cd zlib-1.2.11 && \
    ./configure --static --prefix=/musl-gcc-tmp && \
    make && \
    make install

COPY --from=java-builder concourse-github-app-token/build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --libc=musl --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM alpine:3.14.2@sha256:e1c082e3d3c45cccac829840a25941e679c25d438cc8412c2fa221cf1a824e6a
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
