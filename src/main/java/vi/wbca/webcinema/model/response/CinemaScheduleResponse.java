package vi.wbca.webcinema.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinemaScheduleResponse {
    private Long cinemaId;
    private String cinemaName;
    private List<String> showtimes;
}
