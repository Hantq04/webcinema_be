package vi.wbca.webcinema.service.billFoodService;

import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.model.BillFood;

public interface BillFoodService {
    void insertBillFood(BillFoodDTO billFoodDTO);

    void deleteBillFood(Long id);
}
