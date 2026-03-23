package vi.wbca.webcinema.model.dto.cinema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodRevenueDTO {
    String nameOfFood;

    Long totalQuantity;
}
