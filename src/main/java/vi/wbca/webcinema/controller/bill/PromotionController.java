package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.PromotionDTO;
import vi.wbca.webcinema.service.promotionService.PromotionService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotion")
public class PromotionController {
    private static final Logger logger = Logger.getLogger(PromotionController.class.getName());
    private final PromotionService promotionService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertPromotion(@Valid @RequestBody PromotionDTO request) {

        logger.info("----------Web Cinema: Insert New Promotion----------");

        promotionService.insertPromotion(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.PERCENT, request.getPercent().toString());
        responseData.put(Constants.QUANTITY, request.getQuantity().toString());
        responseData.put(Constants.TYPE, request.getPromotionType().toString());
        responseData.put(Constants.START_TIME, request.getStartTime().toString());
        responseData.put(Constants.END_TIME, request.getEndTime().toString());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        responseData.put(Constants.NAME, request.getName());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert promotion successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deletePromotion(@Valid @RequestBody String name) {

        logger.info("----------Web Cinema: Delete New Promotion----------");

        promotionService.deletePromotion(name);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted promotion successfully.", "")
        );
    }
}
