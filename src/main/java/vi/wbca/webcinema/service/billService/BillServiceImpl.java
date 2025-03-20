package vi.wbca.webcinema.service.billService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.dto.BillTicketDTO;
import vi.wbca.webcinema.enums.EBillStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillMapper;
import vi.wbca.webcinema.model.*;
import vi.wbca.webcinema.repository.*;
import vi.wbca.webcinema.service.billFoodService.BillFoodService;
import vi.wbca.webcinema.service.billTicketService.BillTicketService;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.util.Date;
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

    @Override
    public void createBill(BillDTO billDTO) {
        User user = getCustomer(billDTO);
        BillStatus pendingStatus = getStatus(EBillStatus.PENDING.toString());

        if (billRepo.existsByUserAndBillStatus(user, pendingStatus)) {
            throw new AppException(ErrorCode.BILL_EXISTED);
        }

        Bill bill = billMapper.toBill(billDTO);
        bill.setCreateTime(new Date());
        bill.setTradingCode(GenerateCode.generateTradingCode());
        bill.setName("Bill - " + user.getUsername());
        bill.setUpdateTime(new Date());
        bill.setActive(true);
        bill.setBillStatus(pendingStatus);
        bill.setUser(user);
        billRepo.save(bill);

        insertBillFood(billDTO, bill);
        insertBillTicket(billDTO, bill);
        calculateTotal(bill, user);
        billDTO.setTotalMoney(bill.getTotalMoney());

        billRepo.save(bill);
        billMapper.toBillDTO(bill);
    }

    public void insertBillFood(BillDTO billDTO, Bill bill) {
        for (BillFoodDTO billFoodDTO : billDTO.getFoods()) {
            billFoodDTO.setCustomerName(billDTO.getCustomerName());
            billFoodService.insertBillFood(billFoodDTO, bill);
        }
    }

    public void insertBillTicket(BillDTO billDTO, Bill bill) {
        for (BillTicketDTO billTicketDTO : billDTO.getTickets()) {
            billTicketDTO.setCustomerName(billDTO.getCustomerName());
            billTicketService.insertBillTicket(billTicketDTO, bill);
        }
    }

    public void calculateTotal(Bill bill, User user) {
        double totalFood = calculateBillFood(bill);
        double totalTicket = calculateBillTicket(bill);

        double totalMoney = totalFood + totalTicket;

        Promotion promotion = getPromotion(user);

        if (promotion.getQuantity() == 0 || promotion.getEndTime().before(new Date())) {
            promotion.setActive(false);
            promotionRepo.save(promotion);
        }

        if (promotion.isActive()) {
            double discounted = totalMoney * promotion.getPercent() / 100;
            double finalTotal = totalMoney - discounted;

            // Round the total payment
            int totalForPayment = (int) (Math.round(finalTotal / 1000.0) * 1000);
            bill.setTotalMoney((double) totalForPayment);
            bill.setPromotion(promotion);

            promotion.setQuantity(promotion.getQuantity() - 1);
            promotionRepo.save(promotion);
        } else {
            int totalForPayment = (int) (Math.round(totalMoney / 1000.0) * 1000);
            bill.setTotalMoney((double) totalForPayment);
        }
    }

    public double calculateBillFood(Bill bill) {
        List<BillFood> listBillFood = billFoodRepo.findAllByBillId(bill.getId());
        double total = 0;
        for (BillFood billFood : listBillFood) {
            if (billFood.getFood() != null && billFood.getFood().getPrice() != null) {
                total += billFood.getQuantity() * billFood.getFood().getPrice();
            }
        }
        return total;
    }

    public double calculateBillTicket(Bill bill) {
        List<BillTicket> listBillTicket = billTicketRepo.findAllByBillId(bill.getId());
        double total = 0;
        for (BillTicket billTicket : listBillTicket) {
            if (billTicket.getTicket() != null && billTicket.getTicket().getPriceTicket() != null) {
                total += billTicket.getQuantity() * billTicket.getTicket().getPriceTicket();
            }
        }
        return total;
    }

    public Promotion getPromotion(User user) {
        return promotionRepo.findByRankCustomer(user.getRankCustomer())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    public User getCustomer(BillDTO billDTO) {
        return userRepo.findByUserName(billDTO.getCustomerName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    public BillStatus getStatus(String eBillStatus) {
        return billStatusRepo.findByName(eBillStatus)
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
    }

    @Override
    public void updateBill(BillDTO billDTO) {
        User user = getCustomer(billDTO);
        Bill bill = billRepo.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        billFoodService.updateBillFood(billDTO.getFoods(), bill);
        billTicketService.updateBillTicket(billDTO.getTickets(), bill);

        calculateTotal(bill, user);
        bill.setUpdateTime(new Date());

        billDTO.setTotalMoney(bill.getTotalMoney());
        billRepo.save(bill);
    }

    @Override
    public void deleteBill(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));

        billFoodService.deleteBillFood(bill);
        billTicketService.deleteBillTicket(bill);

        billRepo.delete(bill);
    }
}
