# 1단계: 컴파일 및 빌드
# gradle && jdk17 이미지 빌드 - build 별명
FROM gradle:8.3-jdk17 AS build

# 작업 디렉토리 /app 설정
WORKDIR /app

# 필요한 파일 /app/src 에 복사
COPY . .

# Gradle 빌드 실행
RUN gradle bootJar --no-daemon

# 2단계: 이미지 실행
FROM openjdk:17-jdk-slim

# 작업 디렉토리 /app 설정
WORKDIR /app

# 빌드 결과물 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 서비스를 할 8080 포트를 열어준다
EXPOSE 8080

# 컨테이너에서 실행될 명령어 설정
CMD ["java", "-jar", "app.jar"]


#FROM openjdk:17
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]