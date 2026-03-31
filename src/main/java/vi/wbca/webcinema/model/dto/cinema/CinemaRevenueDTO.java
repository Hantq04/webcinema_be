package vi.wbca.webcinema.model.dto.cinema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaRevenueDTO {
    String nameOfCinema;

    String code;

    BigDecimal totalRevenue;
}
