package vi.wbca.webcinema.controller.bill;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.config.vnpay.VNPayService;
import vi.wbca.webcinema.util.Constants;

import java.io.IOException;
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
    public void confirmPayment(@Valid HttpServletRequest request, HttpServletResponse response) throws
            MessagingException, UnsupportedEncodingException, IOException {
        logger.info("----------Web Cinema: Confirm Payment----------");
        int paymentResult = vnPayService.paymentReturn(request);
        String paymentStatus = paymentResult == 1 ? "success" : paymentResult == 0 ? "cancel" : "fail";
        response.sendRedirect(vnPayService.resolveFrontendReturnUrl(request) + "?paymentStatus=" + paymentStatus);
    }
}
