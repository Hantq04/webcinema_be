package vi.wbca.webcinema.controller.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.setting.Banner;
import vi.wbca.webcinema.service.BannerService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/banner")
public class BannerController {
    private static final Logger logger = Logger.getLogger(BannerController.class.getName());
    private final BannerService bannerService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertBanner(@Valid @RequestBody Banner banner) {
        logger.info("----------Web Cinema: Insert New Banner----------");
        bannerService.insertBanner(banner);
        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.IMAGE, banner.getImageUrl());
        responseData.put(Constants.TITLE, banner.getTitle());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert banner successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteBanner(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete Banner----------");

        bannerService.deleteBanner(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted banner successfully.", "")
        );
    }
}
