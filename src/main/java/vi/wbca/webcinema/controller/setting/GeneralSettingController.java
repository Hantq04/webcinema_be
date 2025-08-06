package vi.wbca.webcinema.controller.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.GeneralSettingDTO;
import vi.wbca.webcinema.service.generalSettingService.GeneralSettingService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class GeneralSettingController {
    private static final Logger logger = Logger.getLogger(GeneralSettingController.class.getName());
    private final GeneralSettingService generalSettingService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertSetting(@Valid @RequestBody GeneralSettingDTO request) {

        logger.info("----------Web Cinema: Insert New General Setting----------");

        GeneralSettingDTO responseData = generalSettingService.insertSetting(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert general setting successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteSetting(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete New General Setting----------");

        generalSettingService.deleteSetting(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted general setting successfully.", "")
        );
    }
}
