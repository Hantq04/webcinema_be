package vi.wbca.webcinema.dto.cinema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaRevenueDTO {
    String nameOfCinema;

    String code;

    Double totalRevenue;
}
