package vi.wbca.webcinema.controller.cinema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.cinema.FoodDTO;
import vi.wbca.webcinema.service.FoodService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/food")
public class FoodController {
    private static final Logger logger = Logger.getLogger(FoodController.class.getName());
    private final FoodService foodService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertFood(@Valid @RequestBody FoodDTO request) {
        logger.info("----------Web Cinema: Insert New Food----------");
        FoodDTO responseData = foodService.insertFood(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert food successfully.", responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateFood(@Valid @RequestBody FoodDTO request) {
        logger.info("----------Web Cinema: Update Food----------");
        foodService.updateFood(request);
        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.PRICE, request.getPrice().toString());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        responseData.put(Constants.IMAGE, request.getImage());
        responseData.put(Constants.NAME, request.getNameOfFood());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated food successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteFood(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Food----------");
        foodService.deleteFood(name);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted food successfully.", "")
        );
    }
}
