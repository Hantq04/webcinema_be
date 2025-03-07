package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.service.billService.BillService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill")
public class BillController {
    private static final Logger logger = Logger.getLogger(BillController.class.getName());
    private final BillService billService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertBill(@Valid @RequestBody BillDTO request) {

        logger.info("----------Web Cinema: Insert New Bill----------");

        BillDTO responseData = billService.insertBill(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert bill successfully.", responseData)
        );
    }
}
