# 使用官方的 OpenJDK 基础镜像
FROM openjdk:17-jdk-slim AS build
ARG APPNAME=electerm-sync-server

# 复制源代码
COPY . /${APPNAME}
# 设置工作目录
WORKDIR /${APPNAME}

# 构建应用程序
RUN ./gradlew build
RUN pwd && ls -l ./build/distributions
RUN apt-get update && apt-get install -y unzip
RUN unzip ./build/distributions/${APPNAME}.zip -d /dist

#缩减镜像
FROM openjdk:17-jdk-slim AS runtime
ARG APPNAME=electerm-sync-server
WORKDIR /app

# 将构建的文件复制到镜像中
COPY --from=build /dist/${APPNAME} .
COPY sample.env .env
RUN pwd && ls -la

# 运行应用程序
CMD ["sh", "/app/bin/electerm-sync-server"]