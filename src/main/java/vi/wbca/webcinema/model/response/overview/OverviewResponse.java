package vi.wbca.webcinema.model.response.overview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vi.wbca.webcinema.model.dto.cinema.FoodRevenueDTO;
import vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.model.dto.revenue.RevenueTimePointDTO;
import vi.wbca.webcinema.model.response.PromotionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewResponse {
    @Schema(description = "Ngày thống kê", example = "2024-06-01")
    private LocalDate date;

    @Schema(description = "Doanh thu hôm nay", example = "1500000.00")
    private BigDecimal todayRevenue;

    @Schema(description = "Số vé bán được hôm nay", example = "120")
    private Long todayTicketCount;

    @Schema(description = "Số vé đã đặt hôm nay", example = "150")
    private Long nowShowingMovieCount;

    @Schema(description = "Tỷ lệ lấp đầy ghế hôm nay", example = "0.75")
    private BigDecimal seatOccupancyRate;

    @Schema(description = "Doanh thu trong 7 ngày qua")
    private List<RevenueTimePointDTO> revenueLast7Days;

    @Schema(description = "Top 5 phim có doanh thu cao nhất hôm nay")
    private List<MovieStatisticDTO> topMovies;

    @Schema(description = "Danh sách các đặt chỗ gần đây")
    private List<OverviewRecentBookingResponse> recentBookings;

    @Schema(description = "Doanh thu từ đồ ăn trong 7 ngày qua")
    private List<FoodRevenueDTO> foodRevenueLast7Days;

    @Schema(description = "Danh sách các chương trình khuyến mãi đang hoạt động")
    private List<PromotionResponse> activePromotions;
}