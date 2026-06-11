# 데이터베이스 규칙

## 파일 위치

| 파일 | 경로 |
|---|---|
| DDL (스키마 정의) | `src/main/resources/db/ddl/schema.sql` |
| ERD (Mermaid) | `src/main/resources/db/ddl/schema-erd.mmd` |

ERD는 [mermaid.live](https://mermaid.live) 에서 확인한다.  
DDL 변경 시 ERD도 함께 업데이트한다.

## 타임스탬프 & Soft Delete 필수 규칙

**모든 테이블**은 아래 3개 컬럼을 반드시 포함한다.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | 레코드 생성 일시 |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 최종 수정 일시 |
| `deleted_at` | `TIMESTAMP` | `NULL` | soft delete 일시 (NULL = 유효 레코드) |

- 데이터는 **hard delete하지 않는다** — 삭제 시 `deleted_at` 에 현재 시각을 기록한다.
- 유효 레코드 조회 시 항상 `WHERE deleted_at IS NULL` 조건을 포함한다.

```sql
created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP             COMMENT '생성 일시',
updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP                          COMMENT '최종 수정 일시',
deleted_at  TIMESTAMP NULL                                            COMMENT '삭제 일시 (soft delete)',
```

## DDL 작성 규칙

- 엔진: `InnoDB`, 문자셋: `utf8mb4`
- **모든 컬럼**에 `COMMENT` 를 작성한다 — 허용값, 단위, 용도를 명시
- **모든 테이블**에 블록 주석(`--`)으로 목적과 설명을 작성한다

```sql
-- ────────────────────────────────────────
-- users
--
-- 목적: 서비스 사용자 계정 관리
-- 설명: Apple/Google OAuth 및 이메일 로그인을 지원한다.
-- ────────────────────────────────────────
CREATE TABLE users (
    id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '사용자 고유 식별자 (PK)',
    email VARCHAR(255) NOT NULL               COMMENT '로그인 이메일 주소 (UNIQUE)',
    ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='서비스 사용자 계정';
```

## 조회 안전 규칙

- 리스트를 반환하는 API 는 **항상 페이지네이션**한다 — 전체 조회(`findAll()`)를 노출하지 않는다.
- 대량 변경은 단건 반복 대신 배치 처리를 고려한다.
- 복잡 조회·N+1·Fetch Join 등 쿼리 작성 기준은 `query.md` 를 따른다.

## 네이밍 규칙

| 대상 | 규칙 | 예시 |
|---|---|---|
| 테이블 | `snake_case` 복수형 | `users`, `spot_categories` |
| 컬럼 | `snake_case` | `created_at`, `is_trending` |
| PK | `id` | `id BIGINT AUTO_INCREMENT` |
| FK 컬럼 | `{참조테이블명 단수형}_id` (테이블 전체 이름 기준) | `user_id`, `spot_id`, `spot_category_id`, `spot_hashtag_id` |
| UK 제약 | `uq_{테이블}_{컬럼}` | `uq_users_email` |
| FK 제약 | `fk_{테이블}_{참조테이블}` | `fk_spots_category` |
| 인덱스 | `idx_{테이블}_{컬럼}` | `idx_spots_trending` |
| Boolean | `is_` 접두사 | `is_trending`, `is_active` |
| 시각 | `_at` 접미사 | `created_at`, `updated_at` |

## 타입 선택 기준

| 용도 | 타입 |
|---|---|
| PK / FK | `BIGINT NOT NULL AUTO_INCREMENT` |
| 짧은 코드/열거 | `VARCHAR(50)` |
| 이름/제목 | `VARCHAR(255)` |
| URL / 주소 | `VARCHAR(500)` |
| 긴 텍스트 | `TEXT` |
| 위도/경도 | `DECIMAL(10, 7)` |
| Boolean | `TINYINT(1)` (0 / 1) |
| 생성·수정 시각 (`created_at`, `updated_at`) | `TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP` |
| 삭제 시각 (`deleted_at`) | `TIMESTAMP NULL` |
