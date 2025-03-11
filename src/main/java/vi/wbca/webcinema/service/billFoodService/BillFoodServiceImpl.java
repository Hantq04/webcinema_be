package vi.wbca.webcinema.service.billFoodService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillFoodMapper;
import vi.wbca.webcinema.model.BillFood;
import vi.wbca.webcinema.model.Food;
import vi.wbca.webcinema.repository.BillFoodRepo;
import vi.wbca.webcinema.repository.FoodRepo;

@Service
@RequiredArgsConstructor
public class BillFoodServiceImpl implements BillFoodService{
    private final BillFoodRepo billFoodRepo;
    private final BillFoodMapper billFoodMapper;
    private final FoodRepo foodRepo;

    @Override
    public void insertBillFood(BillFoodDTO billFoodDTO) {
        BillFood billFood = billFoodMapper.toBillFood(billFoodDTO);
        Food food = setFood(billFoodDTO);

        billFood.setFood(food);
        billFoodRepo.save(billFood);
    }

    public Food setFood(BillFoodDTO billFoodDTO) {
        return foodRepo.findByNameOfFood(billFoodDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    @Override
    public void deleteBillFood(Long id) {
        BillFood billFood = billFoodRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_FOUND));
        billFoodRepo.delete(billFood);
    }
}
