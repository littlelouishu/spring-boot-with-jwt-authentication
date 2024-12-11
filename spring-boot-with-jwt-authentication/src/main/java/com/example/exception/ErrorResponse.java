package com.example.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ErrorResponse {
    private String timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    public static class ErrorResponseBuilder {
        private String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
