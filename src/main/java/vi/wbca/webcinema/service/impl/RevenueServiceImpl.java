package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.enums.RevenueGroupByEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.dto.revenue.RevenueTimePointDTO;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillFood;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.repository.bill.BillFoodRepo;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.BillTicketRepo;
import vi.wbca.webcinema.service.RevenueService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    private final BillRepo billRepo;
    private final BillTicketRepo billTicketRepo;
    private final BillFoodRepo billFoodRepo;

    @Override
    public List<RevenueTimePointDTO> getRevenueSummary(LocalDateTime from,
                                                       LocalDateTime to,
                                                       RevenueGroupByEnum groupBy,
                                                       Long cinemaId,
                                                       Long roomId,
                                                       Long movieId) {
        if (from.isAfter(to)) {
            throw new AppException(ErrorCode.DATE_TIME_EXCEPTION);
        }

        List<Bill> bills = getRevenueBills(from, to, cinemaId, roomId, movieId);
        if (bills.isEmpty()) {
            return List.of();
        }

        List<Long> billIds = bills.stream().map(Bill::getId).toList();
        Map<Long, TicketAggregate> ticketAggByBillId = buildTicketAggregates(billIds);
        Map<Long, BigDecimal> foodRawByBillId = buildFoodRevenueByBillId(billIds);
        Map<LocalDate, RevenueBucket> buckets = new TreeMap<>();
        for (Bill bill : bills) {
            TicketAggregate ticketAgg = ticketAggByBillId.getOrDefault(bill.getId(), TicketAggregate.EMPTY);
            if (ticketAgg.ticketCount == 0) {
                continue;
            }

            LocalDateTime revenueTime = bill.getPaidAt();
            if (revenueTime == null) {
                continue;
            }

            LocalDate periodStart = getPeriodStart(revenueTime, groupBy);
            RevenueBucket bucket = buckets.computeIfAbsent(periodStart,
                key -> new RevenueBucket(formatPeriodLabel(key, groupBy)));

            BigDecimal billTotal = bill.getTotalMoney() == null ? BigDecimal.ZERO : bill.getTotalMoney();
            BigDecimal rawTicket = ticketAgg.ticketRevenue;
            BigDecimal rawFood = foodRawByBillId.getOrDefault(bill.getId(), BigDecimal.ZERO);
            BigDecimal rawTotal = rawTicket.add(rawFood);

            BigDecimal ticketShare = BigDecimal.ZERO;
            if (rawTotal.compareTo(BigDecimal.ZERO) > 0) {
                ticketShare = billTotal.multiply(rawTicket)
                    .divide(rawTotal, 2, RoundingMode.HALF_UP);
            }
            BigDecimal foodShare = billTotal.subtract(ticketShare);

            bucket.totalRevenue = bucket.totalRevenue.add(billTotal);
            bucket.ticketRevenue = bucket.ticketRevenue.add(ticketShare);
            bucket.foodRevenue = bucket.foodRevenue.add(foodShare);
            bucket.ticketCount += ticketAgg.ticketCount;
        }

        return buildTimePointList(buckets);
    }

    @Override
    public byte[] exportRevenueSummary(LocalDateTime from,
                                       LocalDateTime to,
                                       RevenueGroupByEnum groupBy,
                                       Long cinemaId,
                                       Long roomId,
                                       Long movieId) {
        List<RevenueTimePointDTO> summary = getRevenueSummary(from, to, groupBy, cinemaId, roomId, movieId);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Revenue");
            writeHeaderRow(sheet);
            writeDataRows(sheet, summary);
            autosizeColumns(sheet, 5);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private List<Bill> getRevenueBills(LocalDateTime from,
                                      LocalDateTime to,
                                      Long cinemaId,
                                      Long roomId,
                                      Long movieId) {
        String successStatusName = BillStatusEnum.SUCCESS.toString();
        boolean hasFilter = cinemaId != null || roomId != null || movieId != null;
        if (!hasFilter) {
            return billRepo.findAllSuccessfulByCreateTimeBetween(from, to, successStatusName);
        }
        return billRepo.findRevenueBills(from, to, successStatusName, cinemaId, roomId, movieId);
    }

    private Map<Long, TicketAggregate> buildTicketAggregates(List<Long> billIds) {
        if (billIds.isEmpty()) {
            return Map.of();
        }
        List<BillTicket> billTickets = billTicketRepo.findAllByBillIdIn(billIds);
        Map<Long, TicketAggregate> result = new HashMap<>();
        for (BillTicket billTicket : billTickets) {
            if (billTicket.getBill() == null) {
                continue;
            }
            long billId = billTicket.getBill().getId();
            TicketAggregate agg = result.computeIfAbsent(billId, key -> new TicketAggregate());
            if (billTicket.getTicket() != null && billTicket.getTicket().getPriceTicket() != null) {
                agg.ticketRevenue = agg.ticketRevenue.add(
                    BigDecimal.valueOf(billTicket.getTicket().getPriceTicket())
                );
            }
            agg.ticketCount += 1;
        }
        return result;
    }

    private Map<Long, BigDecimal> buildFoodRevenueByBillId(List<Long> billIds) {
        if (billIds.isEmpty()) {
            return Map.of();
        }
        List<BillFood> billFoods = billFoodRepo.findAllByBillIdIn(billIds);
        Map<Long, BigDecimal> result = new HashMap<>();
        for (BillFood billFood : billFoods) {
            if (billFood.getBill() == null) {
                continue;
            }
            long billId = billFood.getBill().getId();
            BigDecimal total = result.getOrDefault(billId, BigDecimal.ZERO);
            if (billFood.getFood() != null && billFood.getFood().getPrice() != null && billFood.getQuantity() != null) {
                BigDecimal price = BigDecimal.valueOf(billFood.getFood().getPrice());
                BigDecimal quantity = BigDecimal.valueOf(billFood.getQuantity());
                total = total.add(price.multiply(quantity));
            }
            result.put(billId, total);
        }
        return result;
    }

    private LocalDate getPeriodStart(LocalDateTime dateTime, RevenueGroupByEnum groupBy) {
        LocalDate date = dateTime.toLocalDate();
        if (groupBy == RevenueGroupByEnum.WEEK) {
            return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        if (groupBy == RevenueGroupByEnum.MONTH) {
            return date.withDayOfMonth(1);
        }
        return date;
    }

    private String formatPeriodLabel(LocalDate periodStart, RevenueGroupByEnum groupBy) {
        if (groupBy == RevenueGroupByEnum.WEEK) {
            WeekFields weekFields = WeekFields.ISO;
            int week = periodStart.get(weekFields.weekOfWeekBasedYear());
            int year = periodStart.get(weekFields.weekBasedYear());
            return String.format("%d-W%02d", year, week);
        }
        if (groupBy == RevenueGroupByEnum.MONTH) {
            return YearMonth.from(periodStart).toString();
        }
        return periodStart.toString();
    }

    private List<RevenueTimePointDTO> buildTimePointList(Map<LocalDate, RevenueBucket> buckets) {
        List<RevenueTimePointDTO> result = new ArrayList<>();
        for (RevenueBucket bucket : buckets.values()) {
            result.add(new RevenueTimePointDTO(
                bucket.periodLabel,
                bucket.totalRevenue.setScale(0, RoundingMode.HALF_UP),
                bucket.ticketRevenue.setScale(0, RoundingMode.HALF_UP),
                bucket.foodRevenue.setScale(0, RoundingMode.HALF_UP),
                bucket.ticketCount
            ));
        }
        return result;
    }

    private void writeHeaderRow(Sheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Kỳ");
        header.createCell(1).setCellValue("Tổng Doanh Thu");
        header.createCell(2).setCellValue("Doanh Thu Vé");
        header.createCell(3).setCellValue("Doanh Thu F&B");
        header.createCell(4).setCellValue("Số Vé");
    }

    private void writeDataRows(Sheet sheet, List<RevenueTimePointDTO> data) {
        int rowIndex = 1;
        for (RevenueTimePointDTO item : data) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(item.getPeriod());
            row.createCell(1).setCellValue(safeNumber(item.getTotalRevenue()));
            row.createCell(2).setCellValue(safeNumber(item.getTicketRevenue()));
            row.createCell(3).setCellValue(safeNumber(item.getFoodRevenue()));
            row.createCell(4).setCellValue(item.getTicketCount() == null ? 0 : item.getTicketCount());
        }
    }

    private double safeNumber(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private void autosizeColumns(Sheet sheet, int totalColumns) {
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static class RevenueBucket {
        private final String periodLabel;
        private BigDecimal totalRevenue = BigDecimal.ZERO;
        private BigDecimal ticketRevenue = BigDecimal.ZERO;
        private BigDecimal foodRevenue = BigDecimal.ZERO;
        private long ticketCount = 0;

        private RevenueBucket(String periodLabel) {
            this.periodLabel = periodLabel;
        }
    }

    private static class TicketAggregate {
        private static final TicketAggregate EMPTY = new TicketAggregate();

        private BigDecimal ticketRevenue = BigDecimal.ZERO;
        private long ticketCount = 0;
    }
}
