package com.asyraaf.assignment.common.dto;

public record ApiResponse<T>(boolean success, String message, T data) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(true, message = "success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(false, message, null);
    }
}
