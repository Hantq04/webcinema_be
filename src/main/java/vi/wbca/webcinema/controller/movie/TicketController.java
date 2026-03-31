package vi.wbca.webcinema.controller.movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.request.BookingRequest;
import vi.wbca.webcinema.model.response.BookingResponse;
import vi.wbca.webcinema.model.response.TicketResponse;
import vi.wbca.webcinema.service.TicketService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket")
public class TicketController {
    private static final Logger logger = Logger.getLogger(TicketController.class.getName());
    private final TicketService ticketService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertTicket(@Valid @RequestBody BookingRequest request) {
        logger.info("----------Web Cinema: Insert New Ticket----------");
        BookingResponse responseData = ticketService.insertTicket(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert ticket successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteTicket(@Valid @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Ticket----------");
        ticketService.deleteTicket(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted ticket successfully.", null)
        );
    }

    @GetMapping("/get-all-ticket")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllTicket() {
        logger.info("----------Web Cinema: Get All Ticket----------");
        List<TicketResponse> responseData = ticketService.getAllTicket();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Get all ticket successfully.", responseData)
        );
    }
}
