FROM alpine:3.14.0@sha256:234cb88d3020898631af0ccbbcca9a66ae7306ecd30c9720690858c1b007d2a0 as libstdcplusplus-provider
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

COPY build/libs/concourse-github-app-token.jar /app/concourse-github-app-token.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static --libc=musl --enable-https -H:ReflectionConfigurationFiles=reflect-config.json -jar concourse-github-app-token.jar

FROM alpine:3.14.0@sha256:234cb88d3020898631af0ccbbcca9a66ae7306ecd30c9720690858c1b007d2a0
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
