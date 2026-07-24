package com.picko.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * springdoc 런타임 스펙(/v3/api-docs)을 docs/openapi.json 파일로 export 한다.
 * web/ios/android 3개 클라이언트가 참조하는 단일 계약(contract) 파일이다.
 *
 * <p>운영: API 변경 후 {@code ./gradlew test} 실행 → 바뀐 docs/openapi.json 을 커밋.
 * (docker compose up -d 로 DB/Redis 가 떠 있어야 컨텍스트가 로드된다.)
 *
 * <p>구현 메모: MockMvc 슬라이스(@AutoConfigureMockMvc)는 Spring Boot 4 에서
 * 별도 모듈로 분리돼 기본 클래스패스에 없다. 의존성 추가 없이 견고하게 가도록
 * 실제 포트로 앱을 띄우고(RANDOM_PORT) spring-web 의 RestClient 로 스펙을 가져온다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiExportTest {

    /** 프로젝트 루트 기준 산출 경로. Gradle 테스트 실행 시 작업 디렉토리가 모듈 루트다. */
    private static final Path OUTPUT = Path.of("docs", "openapi.json");

    /** RANDOM_PORT 로 뜬 실제 서블릿 포트. Spring Boot 가 주입하는 표준 프로퍼티. */
    @Value("${local.server.port}")
    private int port;

    @Test
    void exportOpenApiSpec() throws Exception {
        String json = RestClient.create()
                .get()
                .uri("http://localhost:{port}/v3/api-docs", port)
                .retrieve()
                .body(String.class);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode tree = (ObjectNode) mapper.readTree(json);

        // RANDOM_PORT 로 뜬 서버 URL 은 실행마다 바뀌는 휘발성 값이라 diff 노이즈가 된다.
        // 클라이언트는 자기 base URL 을 쓰므로 계약에 불필요 — 제거해 diff 를 안정화한다.
        tree.remove("servers");

        // 안정적 diff 를 위해 pretty-print 후 커밋 경로에 기록한다.
        Files.createDirectories(OUTPUT.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(OUTPUT.toFile(), tree);
    }
}
