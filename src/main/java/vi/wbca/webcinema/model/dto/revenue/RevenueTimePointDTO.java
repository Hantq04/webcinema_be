package vi.wbca.webcinema.model.dto.revenue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueTimePointDTO {
    String period;

    BigDecimal totalRevenue;

    BigDecimal ticketRevenue;

    BigDecimal foodRevenue;

    Long ticketCount;
}
