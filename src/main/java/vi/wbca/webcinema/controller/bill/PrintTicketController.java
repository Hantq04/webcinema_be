package vi.wbca.webcinema.controller.bill;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vi.wbca.webcinema.util.response.ResponseObject;
import vi.wbca.webcinema.service.PrintTicketService;
import vi.wbca.webcinema.util.Constants;

import java.util.Map;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/print-ticket")
public class PrintTicketController {
    private static final Logger logger = Logger.getLogger(PrintTicketController.class.getName());
    private final PrintTicketService printTicketService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy link PDF vé tại quầy")
    public ResponseEntity<ResponseObject> getPrintTicket(@Valid @RequestParam String tradingCode, HttpServletRequest request) {
        logger.info("----------Web Cinema: Get Print Ticket Data----------");
        String pdfUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(request.getContextPath() + "/api/v1/print-ticket/pdf")
                .replaceQueryParam("tradingCode", tradingCode).toUriString();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_print_ticket", null, locale);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ResponseObject(HttpStatus.OK, message, Map.of(
                "tradingCode", tradingCode,
                "pdfUrl", pdfUrl,
                "fileName", "print-ticket-" + tradingCode + ".pdf"
            )));
        }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Tải PDF vé tại quầy")
    public ResponseEntity<byte[]> downloadPrintTicket(@Valid @RequestParam String tradingCode) {
        logger.info("----------Web Cinema: Download Print Ticket PDF----------");
        byte[] pdfBytes = printTicketService.generatePdf(tradingCode);
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_PDF)
            .header("Content-Disposition", ContentDisposition.attachment()
                .filename("print-ticket-" + tradingCode + ".pdf")
                .build().toString())
            .body(pdfBytes);
    }
}