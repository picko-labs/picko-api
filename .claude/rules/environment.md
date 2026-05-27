# 환경변수 관리 규칙

## 파일 역할

| 파일 | 용도 | git 추적 |
|---|---|---|
| `docker.env` | Docker MySQL 컨테이너 환경변수 | 제외 |
| `docker.env.example` | `docker.env` 작성 템플릿 | 포함 |
| `application.yaml` | Spring 설정 및 DB 접속 정보 | 제외 |

## 원칙

- **Docker 관련 환경변수** → `docker.env`
- **Spring 애플리케이션 설정** → `application.yaml` 에 직접 작성
- 자격증명이 포함된 파일(`docker.env`, `application.yaml`)은 git에 커밋하지 않는다

## 로컬 환경 세팅

```bash
# 1. Docker 환경변수 파일 생성
cp docker.env.example docker.env
# docker.env 에 실제 값 입력

# 2. Spring 설정 파일 생성
# application.yaml 을 직접 작성 (application.yaml.example 참고)

# 3. DB 컨테이너 실행
docker compose up -d

# 4. 앱 실행
./gradlew bootRun
```

## docker.env 구조

```dotenv
# MySQL 컨테이너
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=
MYSQL_USER=
MYSQL_PASSWORD=

# healthcheck 전용 계정
MYSQL_HEALTHCHECK_USER=
MYSQL_HEALTHCHECK_PASSWORD=
```

## application.yaml 구조

```yaml
spring:
  application:
    name: picko-api
  datasource:
    url: jdbc:mysql://localhost:3306/{DB}?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username:
    password:
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
```

## 신규 환경변수 추가 시 체크리스트

- [ ] `docker.env` 에 실제 값 추가
- [ ] `docker.env.example` 에 키만 추가 (값 비워둠)
- [ ] `docker-compose.yaml` 에서 참조 (`${VAR_NAME}`)
- [ ] Spring 설정은 `application.yaml` 에 직접 작성
