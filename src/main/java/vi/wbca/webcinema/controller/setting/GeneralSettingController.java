package vi.wbca.webcinema.controller.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.model.setting.GeneralSetting;
import vi.wbca.webcinema.service.GeneralSettingService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class GeneralSettingController {
    private static final Logger logger = Logger.getLogger(GeneralSettingController.class.getName());
    private final GeneralSettingService generalSettingService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertSetting(@Valid @RequestBody GeneralSettingDTO request) {
        logger.info("----------Web Cinema: Insert New General Setting----------");
        GeneralSettingDTO responseData = generalSettingService.insertSetting(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert general setting successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteSetting(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Delete General Setting----------");
        generalSettingService.deleteSetting(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted general setting successfully.", "")
        );
    }

    @GetMapping("/get-all-setting")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllSetting() {
        logger.info("----------Web Cinema: Get All General Setting----------");
        List<GeneralSetting> responseData = generalSettingService.getAllSetting();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Get all general setting successfully.", responseData)
        );
    }
}
