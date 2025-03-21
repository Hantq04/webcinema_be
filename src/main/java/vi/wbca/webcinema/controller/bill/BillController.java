package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.bill.BillDTO;
import vi.wbca.webcinema.dto.bill.BillTicketDTO;
import vi.wbca.webcinema.service.billService.BillService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill")
public class BillController {
    private static final Logger logger = Logger.getLogger(BillController.class.getName());
    private final BillService billService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> createBill(@Valid @RequestBody BillDTO request) {

        logger.info("----------Web Cinema: Insert New Bill----------");

        billService.createBill(request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put(Constants.USER_NAME, request.getCustomerName());
        responseData.put(Constants.LIST_ORDER, request.getFoods());
        responseData.put(Constants.TOTAL_MONEY, request.getTotalMoney());

        // Custom ticket response
        Map<String, Object> ticketInfo = new HashMap<>();
        List<String> ticketCodes = request.getTickets().stream()
                .map(BillTicketDTO::getCode)
                .toList();
        ticketInfo.put(Constants.CODE, ticketCodes);
        ticketInfo.put(Constants.QUANTITY, ticketCodes.size());
        responseData.put(Constants.LIST_TICKET, ticketInfo);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert bill successfully.", responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateBill(@Valid @RequestBody BillDTO request) {

        logger.info("----------Web Cinema: Update Bill----------");

        billService.updateBill(request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put(Constants.USER_NAME, request.getCustomerName());
        responseData.put(Constants.LIST_ORDER, request.getFoods());
        responseData.put(Constants.LIST_TICKET, request.getTickets());
        responseData.put(Constants.TOTAL_MONEY, request.getTotalMoney());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated bill successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteBill(@Valid @RequestParam String tradingCode) {

        logger.info("----------Web Cinema: Delete Bill----------");

        billService.deleteBill(tradingCode);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted bill successfully.", "")
        );
    }
}
