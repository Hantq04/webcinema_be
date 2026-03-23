package vi.wbca.webcinema.controller.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vi.wbca.webcinema.model.entity.setting.Banner;
import vi.wbca.webcinema.service.BannerService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/banner")
public class BannerController {
    private static final Logger logger = Logger.getLogger(BannerController.class.getName());
    private final BannerService bannerService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertBanner(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("title") String title) throws IOException {
        // Save file
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get("D:/project/uploads/").resolve(Objects.requireNonNull(fileName));
        Files.write(filePath, file.getBytes());
        String imageUrl = "http://localhost:8080/uploads/" + fileName;

        Banner banner = new Banner();
        banner.setTitle(title);
        banner.setImageUrl(imageUrl);
        bannerService.insertBanner(banner);
        return ResponseEntity.ok(
                new ResponseObject(HttpStatus.OK, "Insert banner successfully.", banner)
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

    @GetMapping("/get-all-banner")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllBanner() {
        logger.info("----------Web Cinema: Get All Banner----------");
        List<Banner> responseData = bannerService.getAllBanner();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Get all banner successfully.", responseData)
        );
    }
}
