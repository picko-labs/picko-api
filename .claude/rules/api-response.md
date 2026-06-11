# API 응답 & 예외 규칙

## 공통 응답 envelope

모든 응답은 `ApiResponse<T>` 로 감싼다.

```jsonc
// 성공
{ "success": true, "data": { ... }, "error": null }
// 실패
{ "success": false, "data": null, "error": { "code": "SPOT_NOT_FOUND", "message": "..." } }
```

- 컨트롤러 반환 타입: `ResponseEntity<ApiResponse<T>>`
- 성공: `ResponseEntity.ok(ApiResponse.success(data))` — 생성은 `201 + ApiResponse.success(data)`
- 데이터가 없는 명령(삭제·로그아웃 등)도 envelope 를 유지한다 — `ApiResponse.<Void>success(null)` (204 대신 200/201 + 빈 envelope)

참조: `common/response/ApiResponse.java`

## 예외 처리

- 비즈니스 예외는 `BusinessException(ErrorCode)` 만 던진다 — `IllegalArgumentException` 직접 사용 금지.
- 에러 코드·HTTP 상태·기본 메시지는 `ErrorCode` enum 에 정의한다. enum 이름이 응답 `error.code` 가 된다.
- 예외→응답 변환은 `GlobalExceptionHandler`(`@RestControllerAdvice`)가 전담한다 — 컨트롤러/서비스에서 try-catch 로 응답을 만들지 않는다.

| 상황 | 처리 |
|---|---|
| `BusinessException` | `ErrorCode` 의 상태로 변환 |
| `@Valid` 검증 실패 | `400 INVALID_INPUT` |
| 그 외 미처리 예외 | `500 INTERNAL_ERROR` (로그 기록) |

참조: `common/exception/ErrorCode.java`, `common/exception/GlobalExceptionHandler.java`

## ErrorCode 추가 기준

- 도메인별 식별 가능한 실패에 코드를 부여한다 (`USER_NOT_FOUND`, `SPOT_NOT_FOUND` 등).
- 클라이언트가 분기할 필요가 없는 일반 검증 실패는 `INVALID_INPUT` 으로 통합한다.
