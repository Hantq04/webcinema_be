package vi.wbca.webcinema.controller.cinema;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.cinema.FoodDTO;
import vi.wbca.webcinema.model.request.FoodRequest;
import vi.wbca.webcinema.service.FoodService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/food")
public class FoodController {
    private static final Logger logger = Logger.getLogger(FoodController.class.getName());
    private final FoodService foodService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm món ăn mới")
    public ResponseEntity<ResponseObject> insertFood(@Valid @ModelAttribute FoodRequest request) throws IOException {
        logger.info("----------Web Cinema: Insert New Food----------");
        FoodDTO responseData = foodService.insertFood(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_food", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Cập nhật món ăn")
    public ResponseEntity<ResponseObject> updateFood(@Valid @ModelAttribute FoodRequest request) throws IOException {
        logger.info("----------Web Cinema: Update Food----------");
        foodService.updateFood(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa món ăn theo tên")
    public ResponseEntity<ResponseObject> deleteFood(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Food----------");
        foodService.deleteFood(name);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách tất cả món ăn đang hoạt động")
    public ResponseEntity<ResponseObject> getAllFood() {
        logger.info("----------Web Cinema: Get All Food Active----------");
        List<FoodDTO> responseData = foodService.getAllFoodActive();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_food", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
