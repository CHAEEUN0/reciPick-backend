package team3.recipick.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team3.recipick.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiErrorResponse.of("fail", ex.getMessage()));
    }

    // @Valid 유효성 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)  // 첫 번째 에러만 가져오기 (간단하게)
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(ApiErrorResponse.of("fail", errorMessage));
    }
}
