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
    EXPIRED_REFRESH_TOKEN(1105, "Refresh token is already expired. Please login again.", HttpStatus.FORBIDDEN),

    // Throw Exception, code 12**
    USERNAME_EXISTED(1200, "Username is already existed.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1201, "Email is already existed.", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1202, "Phone number is already existed.", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1203, "Role not found.", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1204, "Username not found.", HttpStatus.BAD_REQUEST),
    OTP_NOT_FOUND(1205, "OTP not found.", HttpStatus.BAD_REQUEST),
    USER_ACTIVE(1206, "This account has already been verified. Please, login.", HttpStatus.CONFLICT),
    EXPIRED_OTP(1207, "Your OTP has already expired.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1208, "Email not found.", HttpStatus.BAD_REQUEST),
    STATUS_NOT_FOUND(1209, "Status not found.", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(1210, "Your account has not been verified. Please check your email.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1211, "Login failed. Invalid username or password.", HttpStatus.UNAUTHORIZED),
    PASSWORD_MISMATCH(1212, "Passwords do not match.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1213, "User not found.", HttpStatus.BAD_REQUEST),
    NAME_NOT_FOUND(1214, "Name not found.", HttpStatus.BAD_REQUEST),
    CODE_NOT_FOUND(1215, "Code not found.", HttpStatus.BAD_REQUEST),
    ENUM_NOT_EXIST(1216, "JSON parse error.", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1217, "Data not found.", HttpStatus.BAD_REQUEST),
    TYPE_NOT_FOUND(1218, "Type not found.", HttpStatus.BAD_REQUEST),
    AUTH_TOKEN_EXCEPTION(1219, "Authorization header is missing or invalid.", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(1220, "Token not found.", HttpStatus.BAD_REQUEST),
    RATE_NOT_FOUND(1221, "Rate not found.", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_FOUND(1222, "Schedule not found.", HttpStatus.BAD_REQUEST),
    ROOM_NOT_FOUND(1223,"Room not found.", HttpStatus.BAD_REQUEST),
    SEAT_NOT_FOUND(1224, "Seat not found.", HttpStatus.BAD_REQUEST),
    DUPLICATE_SHOWTIME(1225, "The room already has a scheduled showtime at this time.", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_FOUND(1226,"Promotion not found.", HttpStatus.BAD_REQUEST),
    NULL_POINTER(1227, "Unexpected null value encountered.", HttpStatus.BAD_REQUEST),
    TITLE_NOT_FOUND(1228, "Title not found.", HttpStatus.BAD_REQUEST),

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
    INVALID_ADDRESS(1312, "Address must be at least 4 characters.", HttpStatus.BAD_REQUEST),
    INVALID_DESCRIPTION(1313, "Description must be at least 6 characters.", HttpStatus.BAD_REQUEST),
    INVALID_NAME(1314, "Name must be at least 3 characters.", HttpStatus.BAD_REQUEST),
    INVALID_CODE(1315, "Invalid code.", HttpStatus.BAD_REQUEST),
    INVALID_CAPACITY(1316, "The capacity must be between 50 and 400 seats.", HttpStatus.BAD_REQUEST),
    INVALID_LINE(1317, "Line must be a letter from A to Z.", HttpStatus.BAD_REQUEST),
    INVALID_NUMBER_SEAT(1318, "Seat number must be between 1 and 20.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_PATH(1319, "Invalid image path. Please provide a valid local image file (jpg, jpeg, png, gif, bmp).", HttpStatus.BAD_REQUEST),
    INVALID_PRICE(1320, "Price must be greater than 1000 VND.", HttpStatus.BAD_REQUEST),
    INVALID_DIRECTOR(1321, "Director name must be at least 3 characters.", HttpStatus.BAD_REQUEST),
    INVALID_LANGUAGE(1322, "Language must be at least 3 characters.", HttpStatus.BAD_REQUEST),
    INVALID_TRAILER_FORM(1323, "The trailer link is invalid.", HttpStatus.BAD_REQUEST),
    INVALID_SHOW_TIME(1324, "Invalid show time.", HttpStatus.BAD_REQUEST),
    INVALID_SEAT(1325, "Invalid seat.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
    }
