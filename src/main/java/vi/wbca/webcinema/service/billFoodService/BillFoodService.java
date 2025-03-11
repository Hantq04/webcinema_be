package vi.wbca.webcinema.service.billFoodService;

import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.model.Bill;

public interface BillFoodService {
    void insertBillFood(BillFoodDTO billFoodDTO, Bill bill);

    void deleteBillFood(Bill bill);
}
