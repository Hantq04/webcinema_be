package vi.wbca.webcinema.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vi.wbca.webcinema.model.response.TransactionHistoryDetailResponse;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;
import vi.wbca.webcinema.model.response.UserTransactionHistoryResponse;

public interface TransactionHistoryService {
    Page<TransactionHistoryResponse> getTransactionHistory(String cinemaName, Pageable pageable);

    Page<UserTransactionHistoryResponse> getTransactionHistory(Pageable pageable);

    TransactionHistoryDetailResponse getTransactionHistoryDetail(String tradingCode);
}