docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7,linux/arm/v8 -t cheleb/zulu-openjdk-alpine:21-latest --push . -f AlpineBash

