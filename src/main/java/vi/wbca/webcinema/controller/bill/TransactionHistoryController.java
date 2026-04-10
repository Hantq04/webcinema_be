package vi.wbca.webcinema.controller.bill;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;
import vi.wbca.webcinema.service.TransactionHistoryService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionHistoryController {
	private final TransactionHistoryService historyService;
	private final MessageSource messageSource;

	@GetMapping("/history")
	@PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
	@Operation(summary = "Lịch sử giao dịch người dùng")
	public ResponseEntity<ResponseObject> getTransactionHistory(@RequestParam(required = false) Long cinemaId,
																@RequestParam int page,
																@RequestParam int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
		Page<TransactionHistoryResponse> responseData = historyService.getTransactionHistory(cinemaId, pageable);
		Locale locale = LocaleContextHolder.getLocale();
		String message = messageSource.getMessage("success.get_transaction_history", null, locale);
		return ResponseEntity.status(HttpStatus.OK).body(
				new ResponseObject(HttpStatus.OK, message, responseData)
		);
	}
}
