package vi.wbca.webcinema.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Security and other error, code 11**
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1100, "Invalid message key.", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1101, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1102, "You don't have permission.", HttpStatus.FORBIDDEN),
    INVALID_SIGNATURE(1103, "Invalid JWT signature.", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN(1104, "JWT token is already expired.", HttpStatus.FORBIDDEN),

    // Throw Exception, code 12**
    USERNAME_EXISTED(1200, "Username is already existed.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1201, "Email is already existed.", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1202, "Phone number is already existed.", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1203, "Role not found.", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1204, "Username not found.", HttpStatus.BAD_REQUEST),

    // Validation, code 13**
    NOT_BLANK(1300, "This field cannot be blank.", HttpStatus.BAD_REQUEST),
    NOT_NULL(1301, "This field cannot be null.", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1302, "Username must be at least 3 characters.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1303, "Email must be at least 6 characters.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1304, "Password must be at least 6 characters.", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME_FORM(1305, "Invalid username format.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORM(1306, "Invalid email format.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORM(1307, "Invalid phone number format.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORM(1308, "Invalid password format.", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1309, "Invalid role code.", HttpStatus.BAD_REQUEST),
    NOT_EMPTY(1310, "This field cannot be empty", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS(1311, "Invalid user status.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
    }
