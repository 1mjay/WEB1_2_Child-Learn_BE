# Base image 선택
FROM openjdk:17-jdk-alpine

WORKDIR /app

# 프로덕션 프로파일 활성화
ENV SPRING_PROFILES_ACTIVE=prod

ENV TZ=Asia/Seoul

# 애플리케이션 JAR 파일 복사
COPY ./build/libs/ijuju-*.jar ./ijuju-be.jar

# 컨테이너에서 실행할 명령어 정의
ENTRYPOINT ["java", "-jar", "ijuju-be.jar"]

# 컨테이너 실행 시 사용할 포트
EXPOSE 8080