package vi.wbca.webcinema.service.billService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.enums.EBillStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillMapper;
import vi.wbca.webcinema.model.*;
import vi.wbca.webcinema.repository.*;
import vi.wbca.webcinema.service.billFoodService.BillFoodService;
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

    @Override
    public void createBill(BillDTO billDTO) {
        User user = getCustomer(billDTO);
        Bill bill = billMapper.toBill(billDTO);

        if (billRepo.existsByUser(user)) {
            throw new AppException(ErrorCode.BILL_EXISTED);
        }

        bill.setCreateTime(new Date());
        bill.setTradingCode(GenerateCode.generateTradingCode());
        bill.setName("Bill - " + user.getName());
        bill.setUpdateTime(new Date());
        bill.setActive(true);
        bill.setBillStatus(getStatus(EBillStatus.PENDING.toString()));
        bill.setUser(user);
        billRepo.save(bill);

        insertBillFood(billDTO, bill);
        calculateTotal(bill, user);

        billRepo.save(bill);
        billMapper.toBillDTO(bill);
    }

    public void insertBillFood(BillDTO billDTO, Bill bill) {
        for (BillFoodDTO billFoodDTO: billDTO.getFoods()) {
            billFoodDTO.setCustomerName(billDTO.getCustomerName());
            billFoodService.insertBillFood(billFoodDTO, bill);
        }
    }

    public void calculateTotal(Bill bill, User user) {
        calculateSubTotal(bill);
        Promotion promotion = getPromotion(user);
        if (promotion != null) {
            int discounted = (int) (bill.getTotalMoney() * promotion.getPercent() / 100);
            bill.setTotalMoney(bill.getTotalMoney() - discounted);
            bill.setPromotion(promotion);

            promotion.setQuantity(promotion.getQuantity() - 1);
            promotionRepo.save(promotion);
        }
    }

    public void calculateSubTotal(Bill bill) {
        List<BillFood> listBillFood = billFoodRepo.findAllByBillId(bill.getId());
        double total = 0;
        for (BillFood billFood: listBillFood) {
            if (billFood.getFood() != null && billFood.getFood().getPrice() != null) {
                total += billFood.getQuantity() * billFood.getFood().getPrice();
            }
        }
        bill.setTotalMoney(total);
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
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    @Override
    public void updateBill(BillDTO billDTO) {
        User user = getCustomer(billDTO);
        Bill bill = billRepo.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        bill.setUpdateTime(new Date());
        billRepo.save(bill);
    }

    @Override
    public void deleteBill(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        billFoodService.deleteBillFood(bill);
        billRepo.delete(bill);
    }
}
