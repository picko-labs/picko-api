# API ↔ Client 스펙 교환 프로세스

Picko의 백엔드(picko-api)와 클라이언트(web/ios/android)가 API 스펙을 주고받는 방식을 정의한다.

## 설계 배경

- 개발 인력이 소수(2명 규모)라 **상설 관리포인트를 최소화**한다.
- 커뮤니케이션 비용을 자동화로 방어하는 거버넌스(CI 게이트·배포 파이프라인·리뷰 의식)는 **도입하지 않는다** — 방어할 조직 규모가 없다.
- 대신 **단일 계약 파일 + 각 플랫폼 코드젠**이라는 fail-safe 레버리지만 남긴다. (썩어도 낡은 파일이 될 뿐, 일을 막지 않는다.)

관련 원칙: `picko-web/docs/development-principles.md` (KISS·YAGNI).

## 큰 그림: 단일 계약 파일이 허브

```
picko-api (springdoc)  ──export──▶  docs/openapi.json  ──consume──▶  web
                                    (단일 계약 파일)      ├──────────▶  ios
                                                          └──────────▶  android
```

핵심 원칙: **모두가 `picko-api/docs/openapi.json` 하나만 바라본다.**
API는 이 파일을 *생산*하고, 클라이언트는 이 파일을 *소비*한다. 사람이 Swagger UI를 눈으로 보고 타입을 옮기는 단계가 사라진다.

> 용어 주의: 이 단계는 "코드젠"이 아니라 **export(추출)** 다. springdoc이 이미 런타임(`/v3/api-docs`)에 스펙을 자동 생성하고 있고, 우리는 그것을 파일로 떠서 커밋할 뿐이다. **코드젠은 반대편(클라이언트)** 에서 이 파일을 입력으로 타입을 생성하는 것을 가리킨다.

## 스펙이 오가는 5단계

| 단계 | 주체 | 작업 | 도구 / 산출물 |
|---|---|---|---|
| ① 정의 | API | 엔드포인트 구현 (컨트롤러·DTO) | springdoc이 런타임 스펙 자동 생성 |
| ② Export | API | `./gradlew test` 실행 | `OpenApiExportTest` → `docs/openapi.json` 갱신 |
| ③ Commit | API | 바뀐 `openapi.json`을 코드와 같은 커밋에 push | 계약 파일이 레포에 박제됨 |
| ④ Consume | 각 Client | `openapi.json`을 받아 타입/클라 코드 생성 | web: openapi-typescript · ios: swift-openapi-generator · android: openapi-generator |
| ⑤ 구현 | 각 Client | 생성 타입 **위에** 도메인 로직 작성 | 예) web: `endpoints.ts` 조합 + React Query |

## 계약 배포 방식

현재는 **가장 단순한 방식(A안)**: `openapi.json`을 picko-api 레포에 커밋하고, 각 클라이언트가 그 파일을 참조한다.

- 클라이언트는 특정 커밋 SHA(또는 버전)를 핀하여 재현성을 확보한다.
- 별도 퍼블리시 파이프라인·아티팩트 레지스트리·contract 전용 레포는 두지 않는다(YAGNI).
- 병목이 실제로 생기면 그때 버전 태그 아티팩트(B안)로 승격한다.

## API 개발자 관점 (생산자)

1. 컨트롤러·DTO 구현 — springdoc이 자동으로 스펙에 반영
2. `docker compose up -d`로 DB/Redis 기동 → `./gradlew test`
3. `git diff docs/openapi.json`으로 **계약이 어떻게 바뀌었는지 확인**
4. 코드 + `openapi.json`을 **같은 커밋**에 담는다 (코드와 계약이 어긋나지 않도록)

포인트: `openapi.json`의 diff가 곧 "클라이언트에 전달하는 변경사항"이다. 이 diff를 근거로 클라 담당에게 알린다.

## Client 개발자 관점 (소비자)

1. picko-api의 최신 `openapi.json`을 pull (커밋 SHA/버전 핀)
2. 코드젠 실행 → 타입/클라 코드 재생성. **생성물 diff = 대응해야 할 변경**
3. 컴파일 에러·타입 불일치로 **깨진 지점을 컴파일러가 알려준다** (드리프트가 런타임이 아니라 빌드에서 잡힌다)
4. 생성 타입 위에 UI·로직 구현

## Export 장치 상세

- 위치: `picko-api/src/test/java/com/picko/api/OpenApiExportTest.java`
- 동작: `@SpringBootTest(webEnvironment = RANDOM_PORT)`로 앱을 실제 포트에 띄우고, spring-web의 `RestClient`로 `/v3/api-docs`를 호출해 `docs/openapi.json`에 pretty-print로 저장
- 안정성: 실행마다 바뀌는 `servers`(RANDOM_PORT URL) 필드를 제거해 **diff를 결정적으로** 만든다 (같은 스펙 → 바이트 단위 동일 출력, 검증 완료)
- 방식 선택 이유:
  - 서드파티 `springdoc-openapi-gradle-plugin`은 최신(1.9.0)이 Spring Boot 3.x 기준이라 Boot 4 호환이 미검증이다.
  - MockMvc 슬라이스(`@AutoConfigureMockMvc`)는 **Spring Boot 4에서 별도 모듈로 분리돼 기본 클래스패스에 없다.** 그래서 의존성 추가 없이 이미 있는 도구(spring-web `RestClient` + spring-boot-starter-test)만으로 파일을 떨구는 편이 **Boot 버전 업그레이드에 안 깨진다.**
- 전제: `/v3/api-docs/**`는 `SecurityConfig`에서 `permitAll` 되어 있어야 한다 (현재 충족).

## 향후 확장 (지금은 도입하지 않음)

- **AI 영향분석**: 스펙 diff(③)를 입력받아 web/ios/android에서 손봐야 할 지점을 AI가 브리핑. AI 하네스 고도화 시점에 ③↔④ 사이에 얹는다.
- **breaking-change 게이트(oasdiff)** / **mock 서버 병렬(Prism)** / **contract-first 전환**: 팀 규모가 커지거나 실제 병목이 발생하면 재검토.

현재 프로세스는 이 확장들의 **토대(단일 계약 파일)** 를 먼저 마련하는 데 목적이 있다.
