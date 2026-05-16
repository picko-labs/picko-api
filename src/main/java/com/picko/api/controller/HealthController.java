package com.picko.api.controller;

import com.picko.api.service.HealthCheckService;
import com.picko.api.service.vo.HealthCheckServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private HealthCheckService healthCheckService;

    @GetMapping("health-check")
    public HealthCheckServiceDto healthcheck() {
        return this.healthCheckService.processHealthCheck();
    }
}
