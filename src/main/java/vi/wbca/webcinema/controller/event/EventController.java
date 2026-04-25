package vi.wbca.webcinema.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import vi.wbca.webcinema.model.dto.event.EventDTO;
import vi.wbca.webcinema.model.request.EventRequest;
import vi.wbca.webcinema.service.EventService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController {
    private static final Logger logger = Logger.getLogger(EventController.class.getName());
    private final EventService eventService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm event mới")
    public ResponseEntity<ResponseObject> insertEvent(@Valid @ModelAttribute EventRequest request) throws IOException {
        logger.info("----------Web Cinema: Insert New Event----------");
        EventDTO responseData = eventService.insertEvent(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_event", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa event theo tên")
    public ResponseEntity<ResponseObject> deleteEvent(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Event----------");
        eventService.deleteEvent(name);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/list")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách event đang active")
    public ResponseEntity<ResponseObject> getAllEventActive() {
        logger.info("----------Web Cinema: Get All Event Active----------");
        List<EventDTO> responseData = eventService.getAllEventActive();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_event", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}