package vi.wbca.webcinema.controller.setting;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.service.GeneralSettingService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class GeneralSettingController {
    private static final Logger logger = Logger.getLogger(GeneralSettingController.class.getName());
    private final GeneralSettingService generalSettingService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Thêm cài đặt chung mới")
    public ResponseEntity<ResponseObject> insertSetting(@Valid @RequestBody GeneralSettingDTO request) {
        logger.info("----------Web Cinema: Insert New General Setting----------");
        GeneralSettingDTO responseData = generalSettingService.insertSetting(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_setting", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Cập nhật cài đặt chung hiện hành")
    public ResponseEntity<ResponseObject> updateSetting(@Valid @RequestBody GeneralSettingDTO request) {
        logger.info("----------Web Cinema: Update General Setting----------");
        GeneralSettingDTO responseData = generalSettingService.updateSetting(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa cài đặt chung theo ID")
    public ResponseEntity<ResponseObject> deleteSetting(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Delete General Setting----------");
        generalSettingService.deleteSetting(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all-setting")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả cài đặt chung")
    public ResponseEntity<ResponseObject> getAllSetting() {
        logger.info("----------Web Cinema: Get All General Setting----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_setting", null, locale);
        List<GeneralSetting> responseData = generalSettingService.getAllSetting();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-latest-setting")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy cài đặt chung mới nhất")
    public ResponseEntity<ResponseObject> getLatestSetting() {
        logger.info("----------Web Cinema: Get Latest General Setting----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_setting", null, locale);
        GeneralSettingDTO responseData = generalSettingService.getLatestSetting();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
