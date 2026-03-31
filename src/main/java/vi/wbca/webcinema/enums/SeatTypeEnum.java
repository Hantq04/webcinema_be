package vi.wbca.webcinema.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum SeatTypeEnum {
    STANDARD("Standard", 60000),
    VIP("Vip", 70000),
    SWEET_BOX("Sweet Box", 80000);

    private final String name;
    private final double price;

    public static SeatTypeEnum getByName(String name) {
        for (SeatTypeEnum seatType: values()) {
            if (seatType.name().equalsIgnoreCase(name)) {
                return seatType;
            }
        }
        throw new AppException(ErrorCode.SEAT_NOT_FOUND);
    }

    public static Double getPriceByType(String type) {
        for (SeatTypeEnum seatType: values()) {
            if (seatType.name().equalsIgnoreCase(type)) {
                return seatType.getPrice();
            }
        }
        throw new AppException(ErrorCode.SEAT_NOT_FOUND);
    }
}
