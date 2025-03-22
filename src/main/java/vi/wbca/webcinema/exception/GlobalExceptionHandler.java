package vi.wbca.webcinema.exception;

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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import vi.wbca.webcinema.util.logging.LoggingUtils;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleException(Exception exception) {
        ErrorCode errorCode;

        if (exception instanceof AppException appException) {
            log.info("Error :: Throw Exception");
            log.info("Error Throw Location :: {}", Arrays.toString(Arrays.copyOfRange(appException.getStackTrace(), 0, 3)));

            LoggingUtils.loggingError(exception);
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
                    log.info("Exception: {}", ex.getMessage());
                }
            }

            log.info("Error :: Validation Exception");
            log.info("Error Field :: {}", fieldError);

            LoggingUtils.loggingError(exception);
            return ResponseEntity.status(e.getStatusCode()).body(
                    new ResponseObject(errorCode.getCode(), errorCode.getMessage(), "")
            );
        } else if (exception instanceof BadCredentialsException) {
            errorCode = ErrorCode.UNAUTHENTICATED;
            log.info("Error :: BadCredentials");
        } else if (exception instanceof AccessDeniedException || exception instanceof UsernameNotFoundException) {
            errorCode = ErrorCode.UNAUTHORIZED;
            log.info("Error :: Denied");
        } else if (exception instanceof SignatureException || exception instanceof ExpiredJwtException) {
            errorCode = exception instanceof SignatureException ? ErrorCode.INVALID_SIGNATURE : ErrorCode.EXPIRED_TOKEN;
            log.info("Error :: JWT");
        } else if (exception instanceof HttpMessageNotReadableException) {
            errorCode = ErrorCode.ENUM_NOT_EXISTED;
            log.info("Error :: HttpMessageNotReadable");
        } else if (exception instanceof NullPointerException) {
            errorCode = ErrorCode.NULL_POINTER;
            log.info("Error :: NullPointer");
        } else if (exception instanceof MethodArgumentTypeMismatchException) {
            errorCode = ErrorCode.DATE_FORMAT;
            log.info("Error :: MethodArgumentTypeMismatch");
        }
        else {
            errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
            log.info("Error :: Unhandled");
        }

        log.info("Error Location: {}", Arrays.toString(Arrays.copyOfRange(exception.getStackTrace(), 0, 3)));
        LoggingUtils.loggingError(exception);
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                new ResponseObject(errorCode.getCode(), errorCode.getMessage(), "")
        );
    }
}
