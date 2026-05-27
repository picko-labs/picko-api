package com.picko.api.service.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserServiceDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
