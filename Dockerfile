# 使用官方的 OpenJDK 基础镜像
FROM openjdk:17-jdk-slim AS build

# 复制源代码
COPY . /app
# 设置工作目录
WORKDIR /app

# 构建应用程序
RUN ./gradlew build
RUN pwd && ls -l ./build

#缩减镜像
FROM openjdk:17-jdk-slim AS runtime
WORKDIR /app

# Install dotenvx
RUN apt-get update && apt-get install -y curl
RUN curl -fsS https://dotenvx.sh/ | sh

RUN pwd && ls -l
# 将构建的 JAR 文件复制到镜像中
COPY --from=build /app/build/distributions/electerm-sync-server-java.zip ./
RUN unzip electerm-sync-server-java.zip
COPY sample.env /app/bin/.env

# 设置环境变量
ENV PATH=$PATH:/app/bin

# 运行应用程序
CMD ["dotenvx", "run", "--", "sh", "electerm-sync-server-java"]