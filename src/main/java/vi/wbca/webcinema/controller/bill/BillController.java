package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.service.billService.BillService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill")
public class BillController {
    private static final Logger logger = Logger.getLogger(BillController.class.getName());
    private final BillService billService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> createBill(@Valid @RequestBody BillDTO request) {

        logger.info("----------Web Cinema: Insert New Bill----------");

        billService.createBill(request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put(Constants.USER_NAME, request.getCustomerName());
        responseData.put(Constants.ORDER, request.getFoods());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert bill successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteBill(@Valid @RequestParam String tradingCode) {

        logger.info("----------Web Cinema: Delete Bill----------");

        billService.deleteBill(tradingCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted bill successfully.", "")
        );
    }
}
