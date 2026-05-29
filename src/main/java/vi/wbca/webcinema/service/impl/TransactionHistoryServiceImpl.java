package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.enums.RoleEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.response.TransactionHistoryDetailResponse;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;
import vi.wbca.webcinema.model.response.UserTransactionHistoryResponse;
import vi.wbca.webcinema.mapper.TransactionHistoryMapper;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.BillStatusRepo;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.service.TransactionHistoryService;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
    private final BillRepo billRepo;
    private final BillStatusRepo billStatusRepo;
    private final UserRepo userRepo;
    private final CinemaRepo cinemaRepo;
    private final TransactionHistoryMapper transactionHistoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionHistoryResponse> getTransactionHistory(String cinemaName, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Bill> bills;

        if (currentUser.getRole() == RoleEnum.STAFF) {
            bills = billRepo.findAllByCinemaId(resolveCinemaId(cinemaName), pageable);
        } else {
            bills = cinemaName == null || cinemaName.isBlank() ? billRepo.findAll(pageable)
                    : billRepo.findAllByCinemaId(resolveCinemaId(cinemaName), pageable);
        }

        return bills.map(transactionHistoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserTransactionHistoryResponse> getTransactionHistory(Pageable pageable) {
        User currentUser = getCurrentUser();
        BillStatus successStatus = getSuccessStatus();
        Page<Bill> bills = billRepo.findAllByUserAndBillStatus(currentUser, successStatus, pageable);

        return bills.map(transactionHistoryMapper::toResponseForUserHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionHistoryDetailResponse getTransactionHistoryDetail(String tradingCode) {
        Bill bill = billRepo.findDetailByTradingCode(tradingCode)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        return transactionHistoryMapper.toDetailResponse(bill);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepo.findByUserName(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    private Long resolveCinemaId(String cinemaName) {
        if (cinemaName == null || cinemaName.isBlank()) {
            throw new AppException(ErrorCode.ID_NOT_FOUND);
        }
        Cinema cinema = cinemaRepo.findByNameOfCinemaIgnoreCase(cinemaName)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        return cinema.getId();
    }

    private BillStatus getSuccessStatus() {
        return billStatusRepo.findByName(BillStatusEnum.SUCCESS.name())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
    }
}