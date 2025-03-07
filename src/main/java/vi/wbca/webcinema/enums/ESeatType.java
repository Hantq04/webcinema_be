package vi.wbca.webcinema.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum ESeatType {
    STANDARD(55000),
    VIP(60000),
    DELUXE(70000);

    private final double price;

    public static Double getPriceByType(String type) {
        for (ESeatType seatType: values()) {
            if (seatType.name().equalsIgnoreCase(type)) {
                return seatType.getPrice();
            }
        }
        throw new AppException(ErrorCode.SEAT_NOT_FOUND);
    }
}
