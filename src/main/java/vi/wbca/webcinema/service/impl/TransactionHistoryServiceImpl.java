package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.enums.RoleEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;
import vi.wbca.webcinema.mapper.TransactionHistoryMapper;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.service.TransactionHistoryService;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
    private final BillRepo billRepo;
    private final UserRepo userRepo;
    private final CinemaRepo cinemaRepo;
    private final TransactionHistoryMapper transactionHistoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionHistoryResponse> getTransactionHistory(Long cinemaId, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Bill> bills;

        if (currentUser.getRole() == RoleEnum.USER) {
            bills = billRepo.findAllByUserAndIsActiveTrue(currentUser, pageable);
        } else if (currentUser.getRole() == RoleEnum.STAFF) {
            bills = billRepo.findAllByCinemaIdAndIsActiveTrue(resolveCinemaId(cinemaId), pageable);
        } else {
            bills = cinemaId == null ? billRepo.findAllByIsActiveTrue(pageable)
                    : billRepo.findAllByCinemaIdAndIsActiveTrue(resolveCinemaId(cinemaId), pageable);
        }

        return bills.map(transactionHistoryMapper::toResponse);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepo.findByUserName(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    private Long resolveCinemaId(Long cinemaId) {
        if (cinemaId == null) {
            throw new AppException(ErrorCode.ID_NOT_FOUND);
        }
        Cinema cinema = cinemaRepo.findById(cinemaId)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        return cinema.getId();
    }
}