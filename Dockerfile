# 使用官方OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制打包好的JAR文件到容器中
COPY target/*.jar app.jar

# 创建配置目录
RUN mkdir -p /app/config

# 暴露应用端口
EXPOSE 5001

# 设置JVM参数和启动命令
ENTRYPOINT ["java", \
           "-Djava.security.egd=file:/dev/./urandom", \
           "-XX:+UseContainerSupport", \
           "-XX:MaxRAMPercentage=75.0", \
           "-jar", "/app/app.jar", \
           "--spring.config.location=file:/app/config/application.yml,file:/app/config/application-${SPRING_PROFILES_ACTIVE:dev}.yml"]