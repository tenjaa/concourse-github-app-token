FROM alpine:3.13.1@sha256:08d6ca16c60fe7490c03d10dc339d9fd8ea67c6466dea8d558526b1330a85930 as libstdcplusplus-provider
RUN apk add g++

FROM ghcr.io/graalvm/graalvm-ce:latest@sha256:626b5b59717e950ab122a5d706ec2e9905e86ef3a45b3e20e41b66205afcabd2 as build
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

FROM alpine:3.13.1@sha256:08d6ca16c60fe7490c03d10dc339d9fd8ea67c6466dea8d558526b1330a85930
COPY --from=build /app/concourse-github-app-token /opt/resource/resource
COPY opt/resource opt/resource
