# 코드 컨벤션

## 패키지 & 클래스 네이밍

| 레이어 | 위치 | 네이밍 |
|---|---|---|
| Controller | `controller/` | `{Domain}Controller` |
| Service | `service/` | `{Domain}Service` |
| DTO | `service/vo/` | `{Domain}ServiceDto` |
| Entity | `repository/entity/` | `{Domain}Entity` |
| Repository | `repository/` | `{Domain}Repository` |

## DTO 패턴

서비스 DTO는 클래스 하나에 `Request` / `Response` 를 내부 클래스로 정의한다.

```java
// service/vo/UserServiceDto.java
public class UserServiceDto {

    @Getter
    @Builder
    public static class Request {
        private String name;
        private String email;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String email;
    }
}
```

## Entity 패턴

```java
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
```

## Controller 응답

모든 엔드포인트는 `ResponseEntity<T>` 로 반환한다.

```java
@GetMapping("/{id}")
public ResponseEntity<UserServiceDto.Response> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUser(id));
}
```

## Lombok 사용 원칙

- Entity: `@Getter` `@Setter` `@NoArgsConstructor`
- DTO Request: `@Getter` `@Builder`
- DTO Response: `@Getter` `@Builder`
- Service/Controller: `@RequiredArgsConstructor` (의존성 주입)
- `@Data` 사용 금지 — 의도하지 않은 `equals`/`hashCode` 생성 방지

## 주석 규칙

- 코드 주석은 **WHY가 명확히 비자명한 경우**에만 작성한다.
- WHAT을 설명하는 주석은 작성하지 않는다.
- JavaDoc은 public API에 한해 작성하고, 구현 상세는 생략한다.
