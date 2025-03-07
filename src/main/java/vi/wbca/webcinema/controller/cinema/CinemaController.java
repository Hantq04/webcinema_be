package vi.wbca.webcinema.controller.cinema;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.CinemaDTO;
import vi.wbca.webcinema.groupValidate.cinema.DeleteCinema;
import vi.wbca.webcinema.groupValidate.cinema.InsertCinema;
import vi.wbca.webcinema.groupValidate.cinema.UpdateCinema;
import vi.wbca.webcinema.service.cinemaService.CinemaService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cinema")
public class CinemaController {
    private static final Logger logger = Logger.getLogger(CinemaController.class.getName());
    private final CinemaService cinemaService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertCinema(@Validated(InsertCinema.class) @RequestBody CinemaDTO request) {

        logger.info("----------Web Cinema: Insert New Cinema----------");

        CinemaDTO responseData = cinemaService.insertCinema(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert cinema successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateCinema(@Validated(UpdateCinema.class) @RequestBody CinemaDTO request) {

        logger.info("----------Web Cinema: Update Cinema----------");

        cinemaService.updateCinema(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.CODE, request.getCode());
        responseData.put(Constants.NAME, request.getNameOfCinema());
        responseData.put(Constants.ADDRESS, request.getAddress());
        responseData.put(Constants.DESCRIPTION, request.getDescription());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated cinema successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteCinema(@Validated(DeleteCinema.class) @RequestParam String code) {

        logger.info("----------Web Cinema: Delete Cinema----------");

        cinemaService.deleteCinema(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted cinema successfully.", "")
        );
    }
}
