package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.enums.RevenueGroupByEnum;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.mapper.OverviewMapper;
import vi.wbca.webcinema.model.response.PromotionResponse;
import vi.wbca.webcinema.model.response.overview.OverviewRecentBookingResponse;
import vi.wbca.webcinema.model.response.overview.OverviewResponse;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.PromotionRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.service.BillFoodService;
import vi.wbca.webcinema.service.MovieService;
import vi.wbca.webcinema.service.OverviewService;
import vi.wbca.webcinema.service.RevenueService;
import vi.wbca.webcinema.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OverviewServiceImpl implements OverviewService {
    private final BillRepo billRepo;
    private final PromotionRepo promotionRepo;
    private final ScheduleRepo scheduleRepo;
    private final RevenueService revenueService;
    private final BillFoodService billFoodService;
    private final MovieService movieService;
    private final OverviewMapper overviewMapper;

    @Override
    @Transactional(readOnly = true)
    public OverviewResponse getOverview() {
        LocalDate today = LocalDate.now(Constants.HO_CHI_MINH);
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        LocalDateTime fromSevenDaysAgo = today.minusDays(6).atStartOfDay();

        List<Bill> todayBills = billRepo.findAllSuccessfulByCreateTimeBetween(startOfToday, endOfToday, BillStatusEnum.SUCCESS.name());
        long todayTicketCount = todayBills.stream()
                .mapToLong(bill -> bill.getBillTickets() == null ? 0 : bill.getBillTickets().size())
                .sum();

        BigDecimal todayRevenue = todayBills.stream()
                .map(Bill::getTotalMoney)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalCapacity = scheduleRepo.findActiveSchedulesOverlapping(startOfToday, endOfToday).stream()
                .mapToLong(schedule -> schedule.getRoom() == null || schedule.getRoom().getCapacity() == null
                        ? 0 : schedule.getRoom().getCapacity()).sum();

        BigDecimal seatOccupancyRate = totalCapacity == 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(todayTicketCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCapacity), 1, RoundingMode.HALF_UP);

        List<OverviewRecentBookingResponse> recentBookings = billRepo.findTop5ByBillStatus_NameAndIsActiveTrueAndPaidAtIsNotNullOrderByPaidAtDesc(BillStatusEnum.SUCCESS.name())
                .stream().map(overviewMapper::toRecentBookingResponse).toList();

        List<PromotionResponse> activePromotions = promotionRepo.findAll().stream()
                .filter(promotion -> {
                    if (promotion == null || !promotion.isActive()) {
                        return false;
                    }
                    LocalDateTime now = LocalDateTime.now(Constants.HO_CHI_MINH);
                    boolean afterStart = promotion.getStartTime() == null || !promotion.getStartTime().isAfter(now);
                    boolean beforeEnd = promotion.getEndTime() == null || !promotion.getEndTime().isBefore(now);
                    return afterStart && beforeEnd;
                })
                .sorted(Comparator.comparing(Promotion::getEndTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(overviewMapper::toPromotionResponse)
                .toList();

        return OverviewResponse.builder()
                .date(today)
                .todayRevenue(todayRevenue)
                .todayTicketCount(todayTicketCount)
                .nowShowingMovieCount((long) movieService.getNowShowingMovies().size())
                .seatOccupancyRate(seatOccupancyRate)
                .revenueLast7Days(revenueService.getRevenueSummary(fromSevenDaysAgo, endOfToday, RevenueGroupByEnum.DAY, null, null, null))
                .topMovies(movieService.sortMovieByTicketOrder(PageRequest.of(0, 5)).getContent())
                .recentBookings(recentBookings)
                .foodRevenueLast7Days(billFoodService.getFoodRevenueSevenDays(fromSevenDaysAgo, endOfToday))
                .activePromotions(activePromotions)
                .build();
    }
}