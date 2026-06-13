package com.picko.api.common.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupLogger {

    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() throws IOException {
        String port = env.getProperty("local.server.port", env.getProperty("server.port", "8080"));
        String profile = String.join(", ", env.getActiveProfiles());
        if (profile.isBlank()) profile = "local";
        String dbUrl = env.getProperty("spring.datasource.url", "-");
        String dbSummary = dbUrl.replaceFirst("jdbc:[^:]+://", "").split("\\?")[0];

        String banner = StreamUtils.copyToString(
                new ClassPathResource("banner.txt").getInputStream(), StandardCharsets.UTF_8);

        log.info("\n{}\n  Picko API  |  {}  |  port: {}\n  DB  : {}\n=========================================",
                banner.stripTrailing(), profile, port, dbSummary);
    }
}
