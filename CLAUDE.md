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
| Etc | Redis, WebSocket, Lombok, Validation |

## 패키지 구조

```
com.picko.api
├── controller/          # REST 엔드포인트
├── service/
│   └── vo/              # 서비스 계층 DTO (Request / Response)
└── repository/
    ├── entity/          # JPA Entity
    │   └── id/          # 복합 PK (@Embeddable)
    └── dao/             # QueryDSL / 커스텀 쿼리 (필요 시)
```

## 빠른 참조

- **로컬 DB 실행**: `docker compose up -d`
- **앱 실행 전 필수**: `docker.env`, `application.yaml` 존재 여부 확인
- **DDL 위치**: `src/main/resources/db/ddl/schema.sql`
- **ERD**: `src/main/resources/db/ddl/schema-erd.mmd` (mermaid.live)
- **API 문서**: 앱 실행 후 `http://localhost:8080/swagger-ui/index.html`

## Skills

| 커맨드 | 설명 |
|---|---|
| `/commit-draft` | 변경사항을 분석해 git-convention 규칙이 적용된 커밋 메시지 초안 생성 |

## Rules

@.claude/rules/code-conventions.md
@.claude/rules/database.md
@.claude/rules/environment.md
@.claude/rules/git-convention.md
