package vi.wbca.webcinema.util;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Constants {

    // Role code
    public static final String ADMIN = "ADMIN";
    public static final String STAFF = "STAFF";
    public static final String USER = "USER";

    // Role combinations for @PreAuthorize
    public static final String PERM_ADMIN_ONLY = "hasRole('" + ADMIN + "')";
    public static final String PERM_STAFF_ADMIN = "hasAnyRole('" + STAFF + "', '" + ADMIN + "')";
    public static final String PERM_USER_STAFF_ADMIN = "hasAnyRole('" + USER + "', '" + STAFF + "', '" + ADMIN + "')";

    // Role name
    public static final String ROLE_ADMIN_NAME = "Administrator";
    public static final String ROLE_STAFF_NAME = "Staff user";
    public static final String ROLE_USER_NAME = "Regular user";

    public static final long BILL_HOLD_MINUTES = 5L;
    public static final Duration BILL_HOLD_DURATION = Duration.ofMinutes(BILL_HOLD_MINUTES);
    public static final long VNPAY_EXPIRE_GRACE_SECONDS = 3L;
    public static final String FRONTEND_PAYMENT_RETURN_URL = "http://localhost:4200/home";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.1);
}
