package vi.wbca.webcinema.util;

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

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
