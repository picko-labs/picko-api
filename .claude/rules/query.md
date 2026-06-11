# 쿼리 규칙

## JPA vs QueryDSL 선택 기준

| 상황 | 사용 |
|---|---|
| 단건 조회, 단순 조건(PK·UK·소수 필드) | Spring Data JPA 메서드 (`findById`, `findByEmail` 등) |
| 동적 조건·다중 필터·정렬·조인 가공이 필요한 복잡 조회 | QueryDSL |

- JPQL 문자열 쿼리(`@Query`)는 지양한다 — 동적 조건이 필요하면 QueryDSL 로 작성한다.
- Native Query 는 불가피한 경우(공간 연산 등)에만 사용하고, 사유를 주석으로 남긴다.

## Repository 분리

| 종류 | 네이밍 | 위치 | 역할 |
|---|---|---|---|
| 기본 Repository | `{Domain}Repository` | `{context}/infrastructure/` | Spring Data JPA 인터페이스 |
| 조회 전용 Repository | `{Domain}QueryRepository` | `{context}/infrastructure/` | QueryDSL 복잡 조회 |

- 복잡 조회 로직은 `{Domain}Repository` 에 섞지 않고 `{Domain}QueryRepository` 로 분리한다.
- `QueryRepository` 는 `JPAQueryFactory` 를 주입받아 사용한다.

참조: `common/config/QuerydslConfig.java` — `JPAQueryFactory` 빈 등록

## Q클래스

- 생성 경로: `build/generated/querydsl` (`build.gradle` 에서 `sourceSets` 로 등록)
- 엔티티 변경 후 Q클래스가 갱신되지 않으면 `./gradlew compileJava` 로 재생성한다.

## 성능 규칙

- 연관 엔티티 조회 시 **N+1 발생 가능성**을 항상 점검한다.
- 컬렉션·연관을 함께 로딩해야 하면 Fetch Join 을 사용한다.
- 리스트 조회는 페이지네이션을 적용한다 — `database.md` 참조.
