FROM oracle/graalvm-ce:20.1.0-java11 as build
RUN gu install native-image
COPY build/libs/test.jar /app/test.jar
COPY reflect-config.json /app/reflect-config.json
RUN cd /app; native-image --no-fallback --static -H:ReflectionConfigurationFiles=reflect-config.json -jar test.jar

FROM alpine:3.12.0
COPY --from=build /app/test /
COPY opt/resource opt/resource
CMD ["sh"]
