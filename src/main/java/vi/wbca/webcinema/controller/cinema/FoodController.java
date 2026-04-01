package vi.wbca.webcinema.controller.cinema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.cinema.FoodDTO;
import vi.wbca.webcinema.service.FoodService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/food")
public class FoodController {
    private static final Logger logger = Logger.getLogger(FoodController.class.getName());
    private final FoodService foodService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertFood(@Valid @RequestBody FoodDTO request) {
        logger.info("----------Web Cinema: Insert New Food----------");
        FoodDTO responseData = foodService.insertFood(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_food", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateFood(@Valid @RequestBody FoodDTO request) {
        logger.info("----------Web Cinema: Update Food----------");
        foodService.updateFood(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.PRICE, request.getPrice().toString());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        responseData.put(Constants.IMAGE, request.getImage());
        responseData.put(Constants.NAME, request.getNameOfFood());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteFood(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Food----------");
        foodService.deleteFood(name);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}
