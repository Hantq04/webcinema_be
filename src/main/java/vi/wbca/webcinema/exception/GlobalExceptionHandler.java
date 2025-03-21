package vi.wbca.webcinema.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vi.wbca.webcinema.util.logging.LoggingUtils;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Arrays;
import java.util.logging.Logger;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleException(Exception exception) {
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
        } else if (exception instanceof BadCredentialsException) {
            errorCode = ErrorCode.UNAUTHENTICATED;
            log.error("Error exception: BadCredentials");
        } else if (exception instanceof AccessDeniedException || exception instanceof UsernameNotFoundException) {
            errorCode = ErrorCode.UNAUTHORIZED;
            log.error("Error exception: {}", exception.getMessage());
        } else if (exception instanceof SignatureException || exception instanceof ExpiredJwtException) {
            errorCode = exception instanceof SignatureException ? ErrorCode.INVALID_SIGNATURE : ErrorCode.EXPIRED_TOKEN;
            log.error("Error exception: {}", exception.getMessage());
        } else if (exception instanceof HttpMessageNotReadableException) {
            errorCode = ErrorCode.ENUM_NOT_EXISTED;
            log.error("Error exception: JSON parse error");
        } else if (exception instanceof NullPointerException) {
            errorCode = ErrorCode.NULL_POINTER;
            log.error("NullPointerException caught: {}", exception.getMessage());
        }
        else {
            log.error("Unhandled exception: {}", exception.getMessage());
            errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        }
        log.error("Error Location: {}", Arrays.toString(Arrays.copyOfRange(exception.getStackTrace(), 0, 3)));
        logger.addHandler(LoggingUtils.createLoggingFolder());
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                new ResponseObject(errorCode.getCode(), errorCode.getMessage(), "")
        );
    }
}
