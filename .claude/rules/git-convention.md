# Git 컨벤션

## 브랜치 네이밍

```
<type>/<간결한-설명>
```

| type | 사용 시점 |
|---|---|
| `feat` | 새 기능 개발 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변경 없는 코드 개선 |
| `chore` | 빌드, 설정, 의존성 변경 |
| `docs` | 문서, 주석 |
| `test` | 테스트 코드 |

```bash
feat/spot-pin-api
fix/user-not-found-500
chore/docker-env-split
```

---

## 커밋 메시지

### 형식

```
<type>: <제목>

<본문 — 선택>
```

### 제목 규칙

- `<type>: ` 접두사 필수
- **한국어** 작성 (Spring Boot, WebSocket, Redis, JPA 등 기술 용어는 영어 허용)
- **50자 이하**
- 끝에 **마침표 없음**
- 트레일러(`Co-Authored-By:` 등) **절대 추가 금지**

### 본문 규칙

- **왜** 변경했는가 위주로 작성 — 무엇을 변경했는지는 diff가 말한다
- 필요한 경우만 작성 (생략 가능)

### 예시

```
feat: 장소 핀 API 추가

사용자가 관심 장소를 저장할 수 있도록 핀 기능을 추가했다.
pin_count 는 user_pins 변경 시 spots 테이블에 동기화한다.
```

```
fix: 존재하지 않는 유저 조회 시 500 반환 수정
```

```
chore: docker.env와 application.yaml 환경변수 분리

Docker 전용 변수와 Spring 설정이 .env 한 파일에 혼재해
역할이 불명확했다. docker.env / application.yaml 로 분리한다.
```

---

## PR

- 제목은 커밋 제목 규칙과 동일하게 작성
- 본문에는 변경 요약, 테스트 방법을 포함
- 하나의 PR은 하나의 관심사만 다룬다
