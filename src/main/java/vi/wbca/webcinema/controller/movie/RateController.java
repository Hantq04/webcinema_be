package vi.wbca.webcinema.controller.movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.movie.Rate;
import vi.wbca.webcinema.service.RateService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rate")
public class RateController {
    private static final Logger logger = Logger.getLogger(RateController.class.getName());
    private final RateService rateService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertRate(@Valid @RequestBody Rate rate) {

        logger.info("----------Web Cinema: Insert New Rate----------");

        Rate responseData = rateService.insertRate(rate);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert rate successfully.", responseData)
        );
    }

    @GetMapping("/get-all-rate")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public List<Rate> getAllRate() {

        logger.info("----------Web Cinema: Get All Rate----------");

        return rateService.getAllRate();
    }
}
