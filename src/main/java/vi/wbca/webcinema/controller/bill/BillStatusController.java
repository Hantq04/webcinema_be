package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.bill.BillStatus;
import vi.wbca.webcinema.service.BillStatusService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/status")
public class BillStatusController {
    private static final Logger logger = Logger.getLogger(BillStatusController.class.getName());
    private final BillStatusService billStatusService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertBillStatus(@Valid @RequestBody BillStatus billStatus) {

        logger.info("----------Web Cinema: Insert New Bill Status----------");

        BillStatus responseData = billStatusService.insertBillStatus(billStatus);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert bill status successfully", responseData)
        );
    }

    @GetMapping("/get-all-status")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public List<BillStatus> getAllStatus() {

        logger.info("----------Web Cinema: Get All Bill Status----------");

        return billStatusService.getAllStatus();
    }
}
