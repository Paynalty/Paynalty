package com.paynalty.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }
}
