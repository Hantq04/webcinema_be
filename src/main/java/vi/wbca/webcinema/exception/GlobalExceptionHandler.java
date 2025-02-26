package vi.wbca.webcinema.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vi.wbca.webcinema.util.ResponseObject;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
        ErrorCode errorCode;

        if (exception instanceof AppException appException) {
            return ResponseEntity.status(appException.getErrorCode().getStatusCode()).body(
                    new ResponseObject(appException.getErrorCode().getCode(), appException.getMessage(), "")
            );
        }

        if (exception instanceof MethodArgumentNotValidException e) {
            FieldError fieldError = e.getFieldError();
            errorCode = ErrorCode.INVALID_KEY;
            if (fieldError != null) {
                String enumKey = fieldError.getDefaultMessage();
                try {
                    errorCode = ErrorCode.valueOf(enumKey);
                } catch (IllegalArgumentException ex) {
                    log.error("Exception: {}", ex.getMessage());
                }
            }
            return ResponseEntity.status(e.getStatusCode()).body(
                    new ResponseObject(errorCode.getCode(), errorCode.getMessage(), "")
            );
        }

        log.error("Unhandled exception: {}", exception.getMessage());
        errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        log.error("Error Location: {}", Arrays.toString(Arrays.copyOfRange(exception.getStackTrace(), 0, 3)));
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                new ResponseObject(errorCode.getCode(), errorCode.getMessage(), "")
        );
    }
}
