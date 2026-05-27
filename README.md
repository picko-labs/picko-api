# picko-api

K-SPOT 서비스의 백엔드 API 서버.  
한국의 트렌드 장소(Spot)를 지도 기반으로 탐색·공유하는 플랫폼이다.

## 기술 스택

| 영역 | 스택 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| DB | MySQL 8.0 (InnoDB / utf8mb4) |
| ORM | Spring Data JPA / Hibernate |
| Infra | Docker Compose |
| Docs | SpringDoc OpenAPI 3 |

## 로컬 개발 환경 세팅

### 1. 환경변수 파일 생성

```bash
cp docker.env.example docker.env
```

`docker.env` 를 열고 MySQL 접속 정보를 입력한다.

`application.yaml` 을 직접 작성한다 (`application.yaml.example` 참고).

### 2. DB 컨테이너 실행

```bash
# 컨테이너 시작
docker compose up -d

# 상태 확인
docker compose ps

# 로그 확인
docker compose logs -f mysql
```

### 3. 앱 실행

```bash
./gradlew bootRun
```

API 문서: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Docker Compose 주요 명령어

| 명령어 | 설명 |
|---|---|
| `docker compose up -d` | 컨테이너 백그라운드 실행 |
| `docker compose down` | 컨테이너 중지 및 제거 |
| `docker compose down -v` | 컨테이너 + 볼륨(데이터) 제거 |
| `docker compose ps` | 실행 중인 컨테이너 상태 확인 |
| `docker compose logs -f mysql` | MySQL 로그 스트리밍 |
| `docker compose restart mysql` | MySQL 컨테이너 재시작 |

> `docker compose down -v` 는 DB 데이터가 삭제되므로 주의한다.

---

## 프로젝트 구조

```
src/main/java/com/picko/api
├── controller/          # REST 엔드포인트
├── service/
│   └── vo/              # 서비스 계층 DTO (Request / Response)
└── repository/
    ├── dvo/             # JPA Entity
    └── dao/             # QueryDSL / 커스텀 쿼리 (필요 시)

src/main/resources/db/ddl/
├── schema.sql           # DDL 전체 (MySQL)
└── schema-erd.mmd       # ERD (mermaid.live 에서 시각화)
```
