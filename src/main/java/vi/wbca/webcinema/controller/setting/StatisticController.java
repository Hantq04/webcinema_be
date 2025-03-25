package vi.wbca.webcinema.controller.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.dto.cinema.CinemaRevenueDTO;
import vi.wbca.webcinema.dto.cinema.FoodRevenueDTO;
import vi.wbca.webcinema.service.billFoodService.BillFoodService;
import vi.wbca.webcinema.service.billService.BillService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting/statistic")
public class StatisticController {
    private static final Logger logger = Logger.getLogger(StatisticController.class.getName());
    private final BillService billService;
    private final BillFoodService billFoodService;

    @GetMapping("/cinema-revenue-statistic")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getRevenueByCinema(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        logger.info("----------Web Cinema: Cinema Revenue Statistic");

        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(LocalTime.MAX);
        List<CinemaRevenueDTO> responseData = billService.getRevenueByCinema(fromTime, toTime);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Get cinema revenue statistic successfully.", responseData)
        );
    }

    @GetMapping("/food-revenue-statistic")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getFoodRevenueSevenDays() {

        logger.info("----------Web Cinema: Food Revenue Statistic");

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(6);
        List<FoodRevenueDTO> responseData = billFoodService.getFoodRevenueSevenDays(startTime, endTime);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Get food revenue statistic successfully.", responseData)
        );
    }
}
