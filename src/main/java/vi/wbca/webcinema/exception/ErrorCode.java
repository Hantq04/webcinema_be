package vi.wbca.webcinema.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Security and other error, code 11**
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1100, "Invalid message key.", HttpStatus.BAD_REQUEST),

    // Throw Exception, code 12**
    USERNAME_EXISTED(1200, "Username already existed.", HttpStatus.BAD_REQUEST),

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
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
    }
