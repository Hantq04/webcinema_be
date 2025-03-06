package vi.wbca.webcinema.controller.cinema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.FoodDTO;
import vi.wbca.webcinema.service.foodService.FoodService;
import vi.wbca.webcinema.util.Informations;
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
    public ResponseEntity<ResponseObject> insertFood(@Valid @RequestBody FoodDTO request) {

        logger.info("----------Web Cinema: Insert New Food----------");

        FoodDTO responseData = foodService.insertFood(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert food successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateFood(@Valid @RequestBody FoodDTO request) {

        logger.info("----------Web Cinema: Update Food----------");

        foodService.updateFood(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Informations.PRICE, request.getPrice().toString());
        responseData.put(Informations.DESCRIPTION, request.getDescription());
        responseData.put(Informations.IMAGE, request.getImage());
        responseData.put(Informations.NAME, request.getNameOfFood());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated food successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteFood(@Valid @RequestParam String name) {
        foodService.deleteFood(name);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted food successfully.", "")
        );
    }
}
