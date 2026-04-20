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
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import vi.wbca.webcinema.util.MessageUtils;
import vi.wbca.webcinema.util.logging.LoggingUtils;
import vi.wbca.webcinema.util.response.FieldValidationError;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.*;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final Set<String> REQUIRED_FIELD_MESSAGES = Set.of("NOT_BLANK", "NOT_EMPTY");

    private final MessageUtils messageUtils;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleException(Exception exception) {
        ErrorCode errorCode;

        if (exception instanceof AppException appException) {
            String errorMessages = messageUtils.getMessage(appException.getErrorCode().getMessage());
            log.info("Error :: Throw Exception");
            log.info("Error Throw Location :: {}", Arrays.toString(Arrays.copyOfRange(appException.getStackTrace(), 0, 3)));

            LoggingUtils.loggingError(exception);
            log.error("Full stack trace:", exception);
            if (exception.getCause() != null) {
                log.error("Caused by:", exception.getCause());
            }
            return ResponseEntity.status(appException.getErrorCode().getStatusCode()).body(
                    new ResponseObject(appException.getErrorCode().getCode(), errorMessages, "")
            );
        }

        if (exception instanceof MethodArgumentNotValidException e) {
            errorCode = ErrorCode.VALIDATE_ERROR;
            Object target = e.getBindingResult().getTarget();

            Map<String, FieldValidationError> requiredFieldErrorsByField = new LinkedHashMap<>();
            Map<String, FieldValidationError> otherFieldErrorsByField = new LinkedHashMap<>();

            e.getFieldErrors().forEach(fieldError -> {
                String fieldName = fieldError.getField();
                boolean isRequiredError = REQUIRED_FIELD_MESSAGES.contains(fieldError.getDefaultMessage());

                FieldValidationError mappedError = isRequiredError
                    ? new FieldValidationError(fieldName, messageUtils.getMessage(ErrorCode.NOT_BLANK.getMessage(), resolveFieldLabel(fieldName)))
                    : isSizeMessage(fieldError.getDefaultMessage()) ? new FieldValidationError(fieldName, resolveSizeMessage(target, fieldName))
                        : new FieldValidationError(fieldName, resolveValidationMessage(fieldError.getDefaultMessage()));

                if (isRequiredError) {
                    requiredFieldErrorsByField.put(fieldName, mappedError);
                } else if (!requiredFieldErrorsByField.containsKey(fieldName)) {
                    otherFieldErrorsByField.putIfAbsent(fieldName, mappedError);
                }
            });

            List<FieldValidationError> fieldErrors = !requiredFieldErrorsByField.isEmpty()
                    ? new ArrayList<>(requiredFieldErrorsByField.values()) : new ArrayList<>(otherFieldErrorsByField.values());

            log.info("Error :: Validation Exception");
            log.info("Error Fields :: {}", e.getFieldErrors());

            LoggingUtils.loggingError(exception);
            log.error("Full stack trace:", exception);
            if (exception.getCause() != null) {
                log.error("Caused by:", exception.getCause());
            }
            return ResponseEntity.status(e.getStatusCode()).body(
                    new ResponseObject(errorCode.getCode(), messageUtils.getMessage(errorCode.getMessage()), fieldErrors)
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
        log.error("Full stack trace:", exception);
        if (exception.getCause() != null) {
            log.error("Caused by:", exception.getCause());
        }
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                new ResponseObject(errorCode.getCode(), messageUtils.getMessage(errorCode.getMessage()), "")
        );
    }

    private String resolveFieldLabel(String fieldPath) {
        String defaultLabel = humanizeFieldPath(fieldPath);
        return messageUtils.getMessageOrDefault("path." + fieldPath, defaultLabel);
    }

    private String humanizeFieldPath(String fieldPath) {
        if (fieldPath == null || fieldPath.isBlank()) {
            return "Field";
        }

        String lastSegment = fieldPath.contains(".")
                ? fieldPath.substring(fieldPath.lastIndexOf('.') + 1)
                : fieldPath;

        String normalized = lastSegment
                .replace('_', ' ')
                .replace('-', ' ')
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .trim();

        if (normalized.isEmpty()) {
            return "Field";
        }

        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String resolveValidationMessage(String defaultMessage) {
        ErrorCode fieldErrorCode = ErrorCode.VALIDATE_ERROR;
        try {
            fieldErrorCode = ErrorCode.valueOf(defaultMessage);
        } catch (IllegalArgumentException ex) {
            log.info("Exception: {}", ex.getMessage());
        }
        return messageUtils.getMessage(fieldErrorCode.getMessage());
    }

    private boolean isSizeMessage(String defaultMessage) {
        return "SIZE_RANGE".equals(defaultMessage);
    }

    private String resolveSizeMessage(Object target, String fieldName) {
        Size size = resolveSizeConstraint(target, fieldName);
        if (size == null) {
            return messageUtils.getMessage(ErrorCode.SIZE_RANGE.getMessage(), resolveFieldLabel(fieldName), "", "");
        }

        String fieldLabel = resolveFieldLabel(fieldName);
        return messageUtils.getMessage(ErrorCode.SIZE_RANGE.getMessage(), fieldLabel, size.min(), size.max());
    }

    private Size resolveSizeConstraint(Object target, String fieldName) {
        if (target == null) {
            return null;
        }

        try {
            java.lang.reflect.Field declaredField = target.getClass().getDeclaredField(fieldName);
            return declaredField.getAnnotation(Size.class);
        } catch (NoSuchFieldException ex) {
            log.info("Exception: {}", ex.getMessage());
            return null;
        }
    }
}
