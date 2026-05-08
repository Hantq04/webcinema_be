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
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(LocalTime.MAX);

        List<Bill> todayBills = billRepo.findAllSuccessfulByCreateTimeBetween(startOfToday, endOfToday, BillStatusEnum.SUCCESS.name());
        List<Bill> yesterdayBills = billRepo.findAllSuccessfulByCreateTimeBetween(startOfYesterday, endOfYesterday, BillStatusEnum.SUCCESS.name());
        long todayTicketCount = todayBills.stream()
                .mapToLong(bill -> bill.getBillTickets() == null ? 0 : bill.getBillTickets().size()).sum();
        long yesterdayTicketCount = yesterdayBills.stream()
                .mapToLong(bill -> bill.getBillTickets() == null ? 0 : bill.getBillTickets().size()).sum();

        BigDecimal todayRevenue = todayBills.stream().map(Bill::getTotalMoney)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal yesterdayRevenue = yesterdayBills.stream().map(Bill::getTotalMoney)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalCapacity = scheduleRepo.findActiveSchedulesOverlapping(startOfToday, endOfToday).stream()
                .mapToLong(schedule -> schedule.getRoom() == null || schedule.getRoom().getCapacity() == null
                        ? 0 : schedule.getRoom().getCapacity()).sum();
        long yesterdayTotalCapacity = scheduleRepo.findSchedulesOverlapping(startOfYesterday, endOfYesterday).stream()
                .mapToLong(schedule -> schedule.getRoom() == null || schedule.getRoom().getCapacity() == null
                        ? 0 : schedule.getRoom().getCapacity()).sum();

        BigDecimal seatOccupancyRate = totalCapacity == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(todayTicketCount)
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalCapacity), 1, RoundingMode.HALF_UP);
        BigDecimal yesterdaySeatOccupancyRate = yesterdayTotalCapacity == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(yesterdayTicketCount)
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(yesterdayTotalCapacity), 1, RoundingMode.HALF_UP);

        long nowShowingMovieCount = movieService.getNowShowingMovies().size();

        List<Long> recentBookingIds = billRepo.findRecentSuccessfulBillIds(BillStatusEnum.SUCCESS.name(), fromSevenDaysAgo, endOfToday);
        List<Bill> recentBookingBills = recentBookingIds.isEmpty() ? List.of() : billRepo.findBillsWithOverviewDataByIds(recentBookingIds);
        List<OverviewRecentBookingResponse> recentBookings = recentBookingBills.stream()
                .sorted(Comparator.comparingInt(bill -> recentBookingIds.indexOf(bill.getId())))
                .map(overviewMapper::toRecentBookingResponse).toList();

        List<PromotionResponse> activePromotions = promotionRepo.findAll().stream()
                .filter(promotion -> {
                    if (promotion == null || !promotion.isActive()) {
                        return false;
                    }
                    LocalDateTime currentTime = LocalDateTime.now(Constants.HO_CHI_MINH);
                    boolean afterStart = promotion.getStartTime() == null || !promotion.getStartTime().isAfter(currentTime);
                    boolean beforeEnd = promotion.getEndTime() == null || !promotion.getEndTime().isBefore(currentTime);
                    return afterStart && beforeEnd;
                })
                .sorted(Comparator.comparing(Promotion::getEndTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(overviewMapper::toPromotionResponse).toList();

        return OverviewResponse.builder()
                .date(today)
                .todayRevenue(todayRevenue)
                .todayRevenueChangePercent(calculateChangePercent(todayRevenue, yesterdayRevenue))
                .todayTicketCount(todayTicketCount)
                .todayTicketCountChangePercent(calculateChangePercent(todayTicketCount, yesterdayTicketCount))
                .nowShowingMovieCount(nowShowingMovieCount)
                .seatOccupancyRate(seatOccupancyRate)
                .seatOccupancyRateChangePercent(calculateChangePercent(seatOccupancyRate, yesterdaySeatOccupancyRate))
                .revenueLast7Days(revenueService.getRevenueSummary(fromSevenDaysAgo, endOfToday, RevenueGroupByEnum.DAY, null, null, null))
                .topMovies(movieService.sortMovieByTicketOrder(PageRequest.of(0, 5)).getContent())
                .recentBookings(recentBookings)
                .foodRevenueLast7Days(billFoodService.getFoodRevenueSevenDays(fromSevenDaysAgo, endOfToday))
                .activePromotions(activePromotions)
                .build();
    }

        private BigDecimal calculateChangePercent(BigDecimal current, BigDecimal previous) {
            if (current == null || previous == null) return null;
            if (previous.compareTo(BigDecimal.ZERO) == 0) {
                return current.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
            }
                return current.subtract(previous).multiply(BigDecimal.valueOf(100)).divide(previous, 1, RoundingMode.HALF_UP);
        }

        private BigDecimal calculateChangePercent(long current, long previous) {
            return calculateChangePercent(BigDecimal.valueOf(current), BigDecimal.valueOf(previous));
        }
}