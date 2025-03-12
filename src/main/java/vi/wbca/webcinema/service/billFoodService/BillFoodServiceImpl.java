package vi.wbca.webcinema.service.billFoodService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillFoodMapper;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillFood;
import vi.wbca.webcinema.model.Food;
import vi.wbca.webcinema.repository.BillFoodRepo;
import vi.wbca.webcinema.repository.FoodRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillFoodServiceImpl implements BillFoodService{
    private final BillFoodRepo billFoodRepo;
    private final BillFoodMapper billFoodMapper;
    private final FoodRepo foodRepo;

    @Override
    public void insertBillFood(BillFoodDTO billFoodDTO, Bill bill) {
        BillFood billFood = billFoodMapper.toBillFood(billFoodDTO);
        Food food = setFood(billFoodDTO);

        billFood.setBill(bill);
        billFood.setFood(food);
        billFoodRepo.save(billFood);
    }

    public Food setFood(BillFoodDTO billFoodDTO) {
        return foodRepo.findByNameOfFood(billFoodDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    @Override
    public void updateBillFood(BillFoodDTO billFoodDTO, Bill bill) {
        List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
        for (BillFood billFood: billFoods) {
            billFood.setQuantity(billFoodDTO.getQuantity());
        }
        billFoodRepo.saveAll(billFoods);
    }

    @Override
    public void deleteFood(Long id) {
        BillFood billFood = billFoodRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        billFoodRepo.delete(billFood);
    }

    @Override
    public void deleteBillFood(Bill bill) {
        List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
        billFoodRepo.deleteAll(billFoods);
    }
}
