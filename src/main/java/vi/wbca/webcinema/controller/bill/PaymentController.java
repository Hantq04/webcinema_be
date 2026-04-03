package vi.wbca.webcinema.controller.bill;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.config.vnpay.VNPayService;
import vi.wbca.webcinema.util.Constants;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/payment")
public class PaymentController {
    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());
    private final VNPayService vnPayService;

    @PostMapping("/submit-payment")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Tạo URL thanh toán VNPay")
    public String submitPayment(@Valid @RequestParam String code, HttpServletRequest request) {
        logger.info("----------Web Cinema: Submit Payment----------");
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return vnPayService.createPayment(code, baseUrl);
    }

    @GetMapping("/vnPay-payment")
    @Operation(summary = "Xác nhận kết quả thanh toán VNPay")
    public String confirmPayment(@Valid HttpServletRequest request) throws
            MessagingException, UnsupportedEncodingException {
        logger.info("----------Web Cinema: Confirm Payment----------");
        return vnPayService.paymentReturn(request) == 1 ? "Payment Successful" : "Payment Cancelled";
    }
}
