package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.BillTicketDTO;
import vi.wbca.webcinema.service.billTicketService.BillTicketService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/ticket")
public class BillTicketController {
    private static final Logger logger = Logger.getLogger(BillTicketController.class.getName());
    private final BillTicketService billTicketService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertBillTicket(@Valid @RequestBody BillTicketDTO request) {

        logger.info("----------Web Cinema: Insert New Bill Ticket----------");

        billTicketService.insertBillTicket(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.QUANTITY, request.getQuantity().toString());
        responseData.put(Constants.USER_NAME, request.getCustomerName());
        responseData.put(Constants.TICKET, request.getCode());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert bill ticket successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteBillTicket(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete Bill Ticket----------");

        billTicketService.deleteBillTicket(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted bill ticket successfully.", "")
        );
    }
}
