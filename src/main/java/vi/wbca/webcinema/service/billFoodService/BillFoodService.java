package vi.wbca.webcinema.service.billFoodService;

import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.model.Bill;

import java.util.List;

public interface BillFoodService {
    void insertBillFood(BillFoodDTO billFoodDTO, Bill bill);

    void updateBillFood(List<BillFoodDTO> billFoodDTO, Bill bill);

    void deleteFood(Long id);

    void deleteBillFood(Bill bill);
}
