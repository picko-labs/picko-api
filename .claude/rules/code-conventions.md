# 코드 컨벤션

## 패키지 & 클래스 네이밍

각 Bounded Context(`user`, `admin`, `spot`, `pin`) 내부 구조:

| 레이어 | 위치 | 네이밍 |
|---|---|---|
| Controller | `{context}/presentation/` | `{Domain}Controller` |
| Application Service | `{context}/application/` | `{Domain}Service` |
| DTO | `{context}/application/dto/` | `{Domain}ServiceDto` |
| Entity | `{context}/domain/` | `{Domain}Entity` |
| Value Object | `{context}/domain/vo/` | (도메인 개념 이름 그대로) |
| Repository | `{context}/infrastructure/` | `{Domain}Repository` |

참조: `src/main/java/com/picko/api/user/` — User 컨텍스트 구현 예시

## DTO 패턴

하나의 클래스에 `Request` / `Response` 를 내부 클래스로 정의한다.

참조: `user/application/dto/UserServiceDto.java`

## Entity 패턴

- `BaseEntity` 를 상속해 `createdAt`, `updatedAt`, `deletedAt` 을 공통 관리한다.
- `authProvider` 처럼 허용값이 고정된 필드는 `String` 대신 `Enum` 으로 선언한다.
- 위도·경도처럼 항상 쌍으로 다뤄지는 값은 `@Embeddable` VO 로 포장한다.

### 상태 변경 정책

- **`@Setter` 를 선언하지 않는다.** 무분별한 외부 상태 변경을 막는다.
- **생성**은 정적 팩토리 메서드로 한다 — `UserEntity.ofOAuth(...)`, `SpotEntity.create(...)`.
- **생성 후 상태 변경**은 의도를 드러내는 도메인 메서드로 한다 — `changeProfile(...)`, `assignAddress(...)`.
- 필드 주입이 필요한 연관관계도 도메인 메서드로 표현한다 (`registerByUser`, `registerByAdmin`).

```java
// 지양
spot.setName(name);
// 권장
SpotEntity spot = SpotEntity.create(name, description, imageUrl, isTrending);
```

참조: `user/domain/UserEntity.java`, `spot/domain/SpotEntity.java`

## Application Service 패턴

- 클래스 레벨에 `@Transactional(readOnly = true)` 선언 — 조회 메서드 기본값
- 쓰기 메서드(`create`, `update`, `delete`)에는 `@Transactional` 개별 선언

참조: `user/application/UserService.java`

## Controller 응답

모든 엔드포인트는 `ResponseEntity<T>` 로 반환한다.

## Lombok 사용 원칙

- Entity: `@Getter` `@NoArgsConstructor` (`@Setter` 금지 — 위 Entity 패턴 참조)
- DTO Request: `@Getter` `@Builder` `@NoArgsConstructor` `@AllArgsConstructor`
- DTO Response: `@Getter` `@Builder`
- Service/Controller: `@RequiredArgsConstructor`
- `@Data` 사용 금지 — 의도하지 않은 `equals`/`hashCode` 생성 방지

## 주석 규칙

- WHY가 비자명한 경우에만 작성한다.
- WHAT을 설명하는 주석은 작성하지 않는다.
