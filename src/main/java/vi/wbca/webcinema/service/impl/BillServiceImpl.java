package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.model.dto.bill.BillDTO;
import vi.wbca.webcinema.model.dto.bill.BillFoodDTO;
import vi.wbca.webcinema.model.dto.cinema.CinemaRevenueDTO;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillMapper;
import vi.wbca.webcinema.model.entity.bill.*;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.repository.bill.*;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.service.BillFoodService;
import vi.wbca.webcinema.service.BillService;
import vi.wbca.webcinema.service.BillTicketService;
import vi.wbca.webcinema.service.TicketHoldCleanupService;
import vi.wbca.webcinema.model.response.BillHoldResponse;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepo billRepo;
    private final BillMapper billMapper;
    private final UserRepo userRepo;
    private final BillStatusRepo billStatusRepo;
    private final PromotionRepo promotionRepo;
    private final BillFoodRepo billFoodRepo;
    private final BillFoodService billFoodService;
    private final BillTicketRepo billTicketRepo;
    private final BillTicketService billTicketService;
    private final TicketHoldCleanupService ticketHoldCleanupService;

    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.05);

    @Override
    public BillHoldResponse createBill(BillDTO request) {
        User user = getCustomer(request);
        BillStatus pendingStatus = billStatusRepo.findByName(BillStatusEnum.PENDING.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));

        ticketHoldCleanupService.cleanupExpiredTicketHolds();

        if (billRepo.existsByUserAndBillStatus(user, pendingStatus)) {
            throw new AppException(ErrorCode.BILL_EXISTED);
        }
        Bill bill = billMapper.toBill(request);
        bill.setCreateTime(LocalDateTime.now());
        bill.setTradingCode(GenerateCode.generateTradingCode());
        bill.setName("Bill - " + user.getUsername());
        bill.setUpdateTime(LocalDateTime.now());
        bill.setActive(true);
        bill.setBillStatus(pendingStatus);
        bill.setUser(user);
        billRepo.save(bill);

        insertBillFood(request, bill);
        insertBillTicket(request, bill);
        calculateTotal(bill, request.getPromotionCode());
        request.setTotalMoney(bill.getTotalMoney());

        billRepo.save(bill);
        billMapper.toBillDTO(bill);

        LocalDateTime holdExpiresAt = bill.getCreateTime().plusMinutes(5);
        long remainingSeconds = Math.max(0, Duration.between(LocalDateTime.now(), holdExpiresAt).getSeconds());

        return BillHoldResponse.builder()
            .tradingCode(bill.getTradingCode())
            .createTime(bill.getCreateTime())
            .holdExpiresAt(holdExpiresAt)
            .remainingSeconds(remainingSeconds)
            .build();
    }

    @Override
    public void updateBill(BillDTO billDTO) {
        User user = getCustomer(billDTO);
        Bill bill = billRepo.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        billFoodService.updateBillFood(billDTO.getFoods(), bill);
        billTicketService.updateBillTicket(billDTO.getTickets(), bill);

        calculateTotal(bill, billDTO.getPromotionCode());
        bill.setUpdateTime(LocalDateTime.now());

        billDTO.setTotalMoney(bill.getTotalMoney());
        billRepo.save(bill);
    }

    @Override
    public void cancelBill(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        ticketHoldCleanupService.cancelBill(bill);
    }

    @Override
    public void deleteBill(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));

        ticketHoldCleanupService.expireBill(bill);
        billFoodService.deleteBillFood(bill);
        billRepo.delete(bill);
    }

    @Override
    public List<CinemaRevenueDTO> getRevenueByCinema(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new AppException(ErrorCode.DATE_TIME_EXCEPTION);
        }
        return billRepo.getRevenueWithTime(from, to);
    }

    public void insertBillFood(BillDTO billDTO, Bill bill) {
        for (BillFoodDTO billFoodDTO : billDTO.getFoods()) {
            billFoodDTO.setCustomerName(billDTO.getCustomerName());
            billFoodService.insertBillFood(billFoodDTO, bill);
        }
    }

    public void insertBillTicket(BillDTO billDTO, Bill bill) {
        billTicketService.insertBillTicket(billDTO.getTickets(), bill);
    }

    @Transactional
    public void calculateTotal(Bill bill, String promotionCode) {
        BigDecimal totalFood = calculateBillFood(bill);
        BigDecimal totalTicket = calculateBillTicket(bill);
        BigDecimal totalVat = calculateBillVat(bill);
        BigDecimal totalMoney = totalFood.add(totalTicket).add(totalVat);
        Promotion promotion = getValidPromotion(promotionCode);
        BigDecimal finalTotal = totalMoney;

        if (promotion != null) {
            BigDecimal percent = BigDecimal.valueOf(promotion.getPercent())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            finalTotal = totalMoney.multiply(BigDecimal.ONE.subtract(percent));
            bill.setPromotion(promotion);

            if (promotion.getQuantity() > 0) {
                promotion.setQuantity(promotion.getQuantity() - 1);
                promotionRepo.save(promotion);
            }
        } else {
            bill.setPromotion(null);
        }

        finalTotal = finalTotal.setScale(0, RoundingMode.HALF_UP);
        bill.setTotalMoney(finalTotal);
    }

    private Promotion getValidPromotion(String promotionCode) {
        if (promotionCode == null || promotionCode.isBlank()) {
            return null;
        }
        Promotion promotion = promotionRepo.findByCode(promotionCode).orElse(null);
        if (promotion == null) return null;
        LocalDateTime now = LocalDateTime.now();
        boolean isExpired = promotion.getEndTime().isBefore(now);
        boolean isNotStarted = promotion.getStartTime().isAfter(now);
        boolean isOutOfStock = promotion.getQuantity() <= 0;

        if (isExpired || isNotStarted || isOutOfStock || !promotion.isActive()) {
            return null;
        }
        return promotion;
    }

    public BigDecimal calculateBillFood(Bill bill) {
        List<BillFood> listBillFood = billFoodRepo.findAllByBillId(bill.getId());
        BigDecimal total = BigDecimal.ZERO;

        for (BillFood billFood : listBillFood) {
            if (billFood.getFood() != null && billFood.getFood().getPrice() != null) {
                BigDecimal price = BigDecimal.valueOf(billFood.getFood().getPrice());
                BigDecimal quantity = BigDecimal.valueOf(billFood.getQuantity());
                total = total.add(price.multiply(quantity));
            }
        }
        return total;
    }

    public BigDecimal calculateBillTicket(Bill bill) {
        return billTicketRepo.findAllByBillId(bill.getId()).stream()
                .filter(bt -> bt.getTicket() != null && bt.getTicket().getPriceTicket() != null)
                .map(bt -> BigDecimal.valueOf(bt.getTicket().getPriceTicket()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateBillVat(Bill bill) {
        return billTicketRepo.findAllByBillId(bill.getId()).stream()
            .filter(bt -> bt.getTicket() != null && bt.getTicket().getPriceTicket() != null)
            .map(bt -> BigDecimal.valueOf(bt.getTicket().getPriceTicket())
                .multiply(VAT_RATE)
                .setScale(0, RoundingMode.HALF_UP))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public User getCustomer(BillDTO billDTO) {
        return userRepo.findByUserName(billDTO.getCustomerName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }
}
