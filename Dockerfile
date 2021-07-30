FROM alpine:3.14.0@sha256:adab3844f497ab9171f070d4cae4114b5aec565ac772e2f2579405b78be67c96 as libstdcplusplus-provider
RUN apk add g++

FROM ghcr.io/graalvm/graalvm-ce:latest@sha256:24fb7be20ec879165dbeeb4a5b7d98287607fc8ef3ba07c715da4f0cd44ba4d1 as build
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

COPY build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --libc=musl --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM alpine:3.14.0@sha256:adab3844f497ab9171f070d4cae4114b5aec565ac772e2f2579405b78be67c96
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
