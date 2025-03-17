package vi.wbca.webcinema.config.vnpay;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.config.EmailService;
import vi.wbca.webcinema.enums.EBillStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillStatus;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.BillRepo;
import vi.wbca.webcinema.repository.BillStatusRepo;
import vi.wbca.webcinema.repository.UserRepo;
import vi.wbca.webcinema.util.EmailUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final BillRepo billRepo;
    private final EmailService emailService;
    private final BillStatusRepo billStatusRepo;
    private final UserRepo userRepo;

    public String createPayment(String code, String returnUrl) {
        Bill bill = getBill(code);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String bankCode = "NCB";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        if (bill.getBillStatus().equals(getStatus(EBillStatus.PENDING.toString()))) {
            Map<String, String> vnp_Params = new HashMap<>();

            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf((long) (bill.getTotalMoney() * 100)));
            vnp_Params.put("vnp_CurrCode", "VND");

            vnp_Params.put("vnp_BankCode", bankCode); // Remove to choose another payment method
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//        vnp_Params.put("vnp_OrderInfo", "Code orders:" + vnp_TxnRef); // Maybe another name
            vnp_Params.put("vnp_OrderInfo", bill.getName() + " with trading code " + bill.getTradingCode());
            vnp_Params.put("vnp_OrderType", orderType);

            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", returnUrl + VNPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            var cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            var vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 10);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            var itr = fieldNames.iterator();

            while (itr.hasNext()) {
                var fieldName = itr.next();
                var fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    try {
                        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                        //Build query
                        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                        query.append('=');
                        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            var queryUrl = query.toString();
            var vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        } else {
            if (bill.getBillStatus().equals(getStatus(EBillStatus.SUCCESS.toString()))) {
                throw new AppException(ErrorCode.PAYMENT_SUCCESS);
            }
            else throw new AppException(ErrorCode.PAYMENT_EXCEPTION);
        }
    }

    public int paymentReturn(HttpServletRequest request) throws MessagingException,
            UnsupportedEncodingException {

        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        var vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        var signValue = VNPayConfig.hashAllFields(fields);

        var orderInfo = request.getParameter("vnp_OrderInfo");
        String tradingCode = extractTradingCode(orderInfo);
        Bill bill = getBill(tradingCode);
        User user = getUser(bill);

        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {

                var paymentTime = request.getParameter("vnp_PayDate");
                var transactionId = request.getParameter("vnp_TransactionNo");
                var totalPrice = request.getParameter("vnp_Amount");

                // Extracting attributes from the model and formatting them into a string
                StringBuilder message = new StringBuilder();
                message.append("Order Info: ").append(orderInfo).append(", ");

                // Create a SimpleDateFormat object with the input format
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                // Create a SimpleDateFormat object with the desired output format
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

                try {
                    // Parse the input string to obtain a Date object
                    Date date = inputFormat.parse(paymentTime);

                    // Format the Date object to the desired output format
                    String formattedDate = outputFormat.format(date);

                    message.append("Payment Time: ").append(formattedDate).append(", ");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                message.append("Transaction ID: ").append(transactionId).append(", ");

                // Dividing the totalPrice by 100
                double totalPriceValue = Double.parseDouble(totalPrice); // Parse the String to a double
                double totalPriceDivided = totalPriceValue / 100; // Divide by 100

                // Formatting totalPriceDivided to add separators for thousands and drop trailing zeroes
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                String formattedTotalPrice = decimalFormat.format(totalPriceDivided).replace(",", "");

                message.append("Total Price: ").append(formattedTotalPrice).append(" VND");

                String userEmail = bill.getUser().getEmail();

                // Send the response via email
                sendResponse(message, userEmail);

                bill.setBillStatus(getStatus(EBillStatus.SUCCESS.toString()));
                user.setPoint(calculatePoint(bill, user));
                userRepo.save(user);
                billRepo.save(bill);

                return 1;
            } else {
                bill.setBillStatus(getStatus(EBillStatus.CANCELLED.toString()));
                billRepo.save(bill);

                return 0;
            }
        } else {
            return -1;
        }
    }

    private String extractTradingCode(String orderInfo) {
        String[] parts = orderInfo.split("with trading code");
        return parts.length > 1 ? parts[1].trim() : "";
    }

    public Bill getBill(String code) {
        return billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }

    public BillStatus getStatus(String eBillStatus) {
        return billStatusRepo.findByName(eBillStatus)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    public User getUser(Bill bill) {
        return userRepo.findByUserName(bill.getUser().getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    public int calculatePoint(Bill bill, User user) {
        int newPoint = bill.getTotalMoney().intValue();
        int currentPoint = user.getPoint();
        return currentPoint + newPoint;
    }

    private String formatDetailAsList(String detailText) {
        String[] parts = detailText.split(",");
        StringBuilder listHtml = new StringBuilder();
        for (String part : parts) {
            listHtml.append("<li>").append(part.trim()).append("</li>");
        }
        return listHtml.toString();
    }

    private void sendResponse(StringBuilder detail, String userEmail) throws MessagingException, UnsupportedEncodingException {
        String subject = "VNPay Payment Response";
        String body = EmailUtils.getConfirmPaymentMessage(formatDetailAsList(detail.toString()));
        // Sending the email
        emailService.sendMail(userEmail, subject, body);
    }
}
