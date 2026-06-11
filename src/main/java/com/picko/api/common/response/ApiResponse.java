package com.picko.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 모든 API 응답을 감싸는 공통 envelope.
 * 성공: success=true, data 채움, error=null
 * 실패: success=false, data=null, error 채움 (GlobalExceptionHandler 가 생성)
 */
@Getter
public class ApiResponse<T> {

    @Schema(description = "요청 성공 여부")
    private final boolean success;

    @Schema(description = "응답 데이터 (실패 시 null)")
    private final T data;

    @Schema(description = "에러 정보 (성공 시 null)")
    private final Error error;

    private ApiResponse(boolean success, T data, Error error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, null, new Error(code, message));
    }

    @Getter
    public static class Error {

        @Schema(description = "에러 코드", example = "SPOT_NOT_FOUND")
        private final String code;

        @Schema(description = "에러 메시지")
        private final String message;

        private Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
