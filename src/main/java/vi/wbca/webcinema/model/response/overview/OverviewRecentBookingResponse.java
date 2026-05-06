package vi.wbca.webcinema.model.response.overview;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewRecentBookingResponse {
    private String tradingCode;
    private String customerName;
    private String movieName;
    private String showTimeName;
    private String roomCode;
    private List<String> seatCodes;
    private BigDecimal totalMoney;
    private String billStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createTime;
}