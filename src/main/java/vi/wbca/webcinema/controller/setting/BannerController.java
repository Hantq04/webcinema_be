package vi.wbca.webcinema.controller.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.Banner;
import vi.wbca.webcinema.service.bannerService.BannerService;
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
    public ResponseEntity<ResponseObject> deleteBanner(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete New Banner----------");

        bannerService.deleteBanner(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted banner successfully.", "")
        );
    }
}
