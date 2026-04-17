package com.example.rollback.exception;

import com.example.rollback.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    ResponseEntity<ApiResponse> handlingCustomException(CustomException custom){
        ErrorCode errorCode = custom.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception){
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.CHECKED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.CHECKED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
