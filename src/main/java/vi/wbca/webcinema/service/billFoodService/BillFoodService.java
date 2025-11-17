package vi.wbca.webcinema.service.billFoodService;

import vi.wbca.webcinema.dto.bill.BillFoodDTO;
import vi.wbca.webcinema.dto.cinema.FoodRevenueDTO;
import vi.wbca.webcinema.model.bill.Bill;

import java.time.LocalDateTime;
import java.util.List;

public interface BillFoodService {
    void insertBillFood(BillFoodDTO billFoodDTO, Bill bill);

    void updateBillFood(List<BillFoodDTO> billFoodDTO, Bill bill);

    void deleteFood(Long id);

    void deleteBillFood(Bill bill);

    List<FoodRevenueDTO> getFoodRevenueSevenDays(LocalDateTime start, LocalDateTime end);
}
