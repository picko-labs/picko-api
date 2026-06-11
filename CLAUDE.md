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

Bounded Context 기반 DDD 구조. 각 컨텍스트는 `domain / infrastructure / application / presentation` 으로 구성한다.

```
com.picko.api
├── common/              # 공통 (BaseEntity, Config)
├── user/
├── admin/
├── spot/
└── pin/
```

각 컨텍스트 내부:
```
{context}/
├── domain/              # Entity, VO, Enum, Domain Service
│   └── vo/              # Value Object
├── infrastructure/      # Repository
├── application/         # Application Service (트랜잭션 소유)
│   └── dto/             # Request / Response DTO
└── presentation/        # Controller
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

@.claude/rules/dev-principles.md
@.claude/rules/code-conventions.md
@.claude/rules/api-response.md
@.claude/rules/database.md
@.claude/rules/query.md
@.claude/rules/environment.md
@.claude/rules/git-convention.md

## Rules 관리 원칙

- **기본 상식 제거** — 프레임워크·언어 수준의 일반 지식은 작성하지 않는다. 이 프로젝트의 결정사항만 기록한다.
- **모듈화 + 파일 참조** — 주제별로 파일을 분리하고 `@import`로 연결한다. 하나의 파일에 모든 것을 담지 않는다.
- **예시는 필요한 경우에만** — 복잡도·이해도가 높은 경우에만 스니펫을 사용한다. 소스 파일이 존재하면 파일 경로로 참조한다.
- **500줄 미만 유지** — 초과 시 즉시 검토하고 불필요한 내용을 삭제한다.
- **명확한 헤딩과 글머리 기호** — 에이전트가 구조를 빠르게 파악할 수 있도록 한다.
