package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.BillFoodDTO;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.service.billFoodService.BillFoodService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/food")
public class BillFoodController {
    private static final Logger logger = Logger.getLogger(BillFoodController.class.getName());
    private final BillFoodService billFoodService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertBillFood(@Valid @RequestBody BillFoodDTO request, Bill bill) {

        logger.info("----------Web Cinema: Insert New Bill Food----------");

        billFoodService.insertBillFood(request, bill);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.QUANTITY, request.getQuantity().toString());
        responseData.put(Constants.USER_NAME, request.getCustomerName());
        responseData.put(Constants.NAME, request.getName());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert bill food successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteBillFood(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete Bill Food----------");

        billFoodService.deleteFood(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted bill food successfully.", "")
        );
    }
}
