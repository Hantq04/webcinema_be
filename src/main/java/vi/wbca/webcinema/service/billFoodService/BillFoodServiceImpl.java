package vi.wbca.webcinema.service.billFoodService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.bill.BillFoodDTO;
import vi.wbca.webcinema.dto.cinema.FoodRevenueDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillFoodMapper;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillFood;
import vi.wbca.webcinema.model.Food;
import vi.wbca.webcinema.repository.BillFoodRepo;
import vi.wbca.webcinema.repository.FoodRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Food food = getFood(billFoodDTO);

        billFood.setBill(bill);
        billFood.setFood(food);
        billFoodRepo.save(billFood);
    }

    public Food getFood(BillFoodDTO billFoodDTO) {
        return foodRepo.findByNameOfFood(billFoodDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    @Override
    public void updateBillFood(List<BillFoodDTO> billFoodDTOs, Bill bill) {
        // Retrieve existing BillFood records from the database
        List<BillFood> existingBillFoods = billFoodRepo.findAllByBill(bill);

        // Create a list to store processed foods to keep track of updated/added items
        List<Food> processedFoods = new ArrayList<>();

        for (BillFoodDTO billFoodDTO : billFoodDTOs) {
            Food food = getFood(billFoodDTO);
            processedFoods.add(food);

            // Check if the BillFood already exists in the database
            BillFood billFood = billFoodRepo.findByBillAndFood(bill, food).orElse(null);

            if (billFood != null) {
                billFood.setQuantity(billFoodDTO.getQuantity());
            } else {
                billFood = billFoodMapper.toBillFood(billFoodDTO);
                billFood.setFood(food);
                billFood.setBill(bill);
            }

            billFoodRepo.save(billFood);
        }
        // Delete BillFood records from DB that are not included in the new DTO list
        for (BillFood oldBillFood : existingBillFoods) {
            if (!processedFoods.contains(oldBillFood.getFood())) {
                billFoodRepo.delete(oldBillFood);
            }
        }
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

    @Override
    public List<FoodRevenueDTO> getFoodRevenueSevenDays(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new AppException(ErrorCode.DATE_TIME_EXCEPTION);
        }
        return billFoodRepo.getFoodRevenueSevenDays(start, end);
    }
}
