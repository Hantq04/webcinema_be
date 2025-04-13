package vi.wbca.webcinema.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Security and other error, code 11**
    UNCATEGORIZED_EXCEPTION(9999, "error.9999", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1100, "error.1100", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1101, "error.1101", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1102, "error.1102", HttpStatus.FORBIDDEN),
    INVALID_SIGNATURE(1103, "error.1103", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN(1104, "error.1104", HttpStatus.FORBIDDEN),
    EXPIRED_REFRESH_TOKEN(1105, "error.1105", HttpStatus.FORBIDDEN),

    // Throw Exception, code 12**
    USERNAME_EXISTED(1200, "error.1200", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1201, "error.1201", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1202, "error.1202", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1203, "error.1203", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1204, "error.1204", HttpStatus.BAD_REQUEST),
    OTP_NOT_FOUND(1205, "error.1205", HttpStatus.BAD_REQUEST),
    USER_ACTIVE(1206, "error.1206", HttpStatus.CONFLICT),
    EXPIRED_OTP(1207, "error.1207", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1208, "error.1208", HttpStatus.BAD_REQUEST),
    STATUS_NOT_FOUND(1209, "error.1209", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(1210, "error.1210", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1211, "error.1211", HttpStatus.UNAUTHORIZED),
    PASSWORD_MISMATCH(1212, "error.1212", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1213, "error.1213", HttpStatus.BAD_REQUEST),
    NAME_NOT_FOUND(1214, "error.1214", HttpStatus.BAD_REQUEST),
    CODE_NOT_FOUND(1215, "error.1215", HttpStatus.BAD_REQUEST),
    ENUM_NOT_EXISTED(1216, "error.1216", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1217, "error.1217", HttpStatus.BAD_REQUEST),
    TYPE_NOT_FOUND(1218, "error.1218", HttpStatus.BAD_REQUEST),
    AUTH_TOKEN_EXCEPTION(1219, "error.1219", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(1220, "error.1220", HttpStatus.BAD_REQUEST),
    RATE_NOT_FOUND(1221, "error.1221", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_FOUND(1222, "error.1222", HttpStatus.BAD_REQUEST),
    ROOM_NOT_FOUND(1223,"error.1223", HttpStatus.BAD_REQUEST),
    SEAT_NOT_FOUND(1224, "error.1224", HttpStatus.BAD_REQUEST),
    DUPLICATE_SHOWTIME(1225, "error.1225", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_FOUND(1226,"error.1226", HttpStatus.BAD_REQUEST),
    NULL_POINTER(1227, "error.1227", HttpStatus.BAD_REQUEST),
    TITLE_NOT_FOUND(1228, "error.1228", HttpStatus.BAD_REQUEST),
    SETTING_NOT_FOUND(1229, "error.1229", HttpStatus.BAD_REQUEST),
    SHOW_TIME_IN_BREAK(1230, "error.1230", HttpStatus.BAD_REQUEST),
    BILL_NOT_FOUND(1231, "error.1231", HttpStatus.BAD_REQUEST),
    BILL_EXISTED(1232, "error.1232", HttpStatus.BAD_REQUEST),
    BILL_FOOD_NOT_FOUND(1233, "error.1233", HttpStatus.BAD_REQUEST),
    ID_NOT_FOUND(1234, "error.1234", HttpStatus.BAD_REQUEST),
    START_TIME_NOT_FOUND(1235, "error.1235", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_BELONG_TO_ROOM(1236, "error.1236", HttpStatus.BAD_REQUEST),
    SEAT_NOT_BELONG_TO_ROOM(1237, "error.1237", HttpStatus.BAD_REQUEST),
    SEAT_OCCUPIED(1238, "error.1238", HttpStatus.BAD_REQUEST),
    ROOM_FULL(1239, "error.1239", HttpStatus.BAD_REQUEST),
    SCHEDULE_EXPIRED(1240, "error.1240", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_EXISTED(1241, "error.1241", HttpStatus.BAD_REQUEST),
    SEAT_EXISTED(1242, "error.1242", HttpStatus.BAD_REQUEST),
    DATE_TIME_EXCEPTION(1243, "error.1243", HttpStatus.BAD_REQUEST),
    DATE_FORMAT(1244, "error.1244", HttpStatus.BAD_REQUEST),

    // Validation, code 13**
    NOT_BLANK(1300, "error.1300", HttpStatus.BAD_REQUEST),
    NOT_NULL(1301, "error.1301", HttpStatus.BAD_REQUEST),
    NOT_EMPTY(1302, "error.1302", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1303, "error.1303", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1304, "error.1304", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1305, "error.1305", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME_FORM(1306, "error.1306", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORM(1307, "error.1307", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORM(1308, "error.1308", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORM(1309, "error.1309", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1310, "error.1310", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS(1311, "error.1311", HttpStatus.BAD_REQUEST),
    INVALID_ADDRESS(1312, "error.1312", HttpStatus.BAD_REQUEST),
    INVALID_DESCRIPTION(1313, "error.1313", HttpStatus.BAD_REQUEST),
    INVALID_NAME(1314, "error.1314", HttpStatus.BAD_REQUEST),
    INVALID_CODE(1315, "error.1315", HttpStatus.BAD_REQUEST),
    INVALID_CAPACITY(1316, "error.1316", HttpStatus.BAD_REQUEST),
    INVALID_LINE(1317, "error.1317", HttpStatus.BAD_REQUEST),
    INVALID_NUMBER_SEAT(1318, "error.1318", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_PATH(1319, "error.1319", HttpStatus.BAD_REQUEST),
    INVALID_PRICE(1320, "error.1320", HttpStatus.BAD_REQUEST),
    INVALID_DIRECTOR(1321, "error.1321", HttpStatus.BAD_REQUEST),
    INVALID_LANGUAGE(1322, "error.1322", HttpStatus.BAD_REQUEST),
    INVALID_TRAILER_FORM(1323, "error.1323", HttpStatus.BAD_REQUEST),
    INVALID_SHOW_TIME(1324, "error.1324", HttpStatus.BAD_REQUEST),
    INVALID_SEAT(1325, "error.1325", HttpStatus.BAD_REQUEST),
    INVALID_START_TIME(1326, "error.1326", HttpStatus.BAD_REQUEST),

    // Payment, code 70***
    PAYMENT_SUCCESS(70010, "error.70010", HttpStatus.BAD_REQUEST),
    PAYMENT_EXCEPTION(70020, "error.70020", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
    }
