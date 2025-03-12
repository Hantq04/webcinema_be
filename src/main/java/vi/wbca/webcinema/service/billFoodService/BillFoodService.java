package vi.wbca.webcinema.service.billFoodService;

import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.model.Bill;

public interface BillFoodService {
    void insertBillFood(BillFoodDTO billFoodDTO, Bill bill);

    void updateBillFood(BillFoodDTO billFoodDTO, Bill bill);

    void deleteFood(Long id);

    void deleteBillFood(Bill bill);
}
