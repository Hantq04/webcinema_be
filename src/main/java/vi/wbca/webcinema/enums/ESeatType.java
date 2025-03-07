package vi.wbca.webcinema.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ESeatType {
    STANDARD(55000),
    VIP(60000),
    DELUXE(70000);

    private final double price;
}
