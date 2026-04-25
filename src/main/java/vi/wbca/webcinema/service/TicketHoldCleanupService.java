package vi.wbca.webcinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.enums.SeatStatusEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.BillStatusRepo;
import vi.wbca.webcinema.repository.bill.BillTicketRepo;
import vi.wbca.webcinema.repository.movie.TicketRepo;
import vi.wbca.webcinema.repository.seat.SeatRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.util.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketHoldCleanupService {
    private final BillRepo billRepo;
    private final BillStatusRepo billStatusRepo;
    private final BillTicketRepo billTicketRepo;
    private final TicketRepo ticketRepo;
    private final SeatRepo seatRepo;
    private final SeatStatusRepo seatStatusRepo;
    private final ObjectProvider<TicketHoldCleanupService> selfProvider;

    @Transactional
    public void cleanupExpiredTicketSelectionHolds() {
        LocalDateTime threshold = LocalDateTime.now().minus(Constants.BILL_HOLD_DURATION);
        List<Ticket> expiredTickets = ticketRepo.findExpiredTicketHolds(threshold);

        if (!expiredTickets.isEmpty()) {
            log.info("Cleaning up {} expired ticket selection hold(s)", expiredTickets.size());
            SeatStatus availableSeatStatus = seatStatusRepo.findByCode(SeatStatusEnum.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
            seatRepo.updateSeatStatusByTicketCodes(
                expiredTickets.stream().map(Ticket::getCode).toList(),
                availableSeatStatus
            );
            ticketRepo.deleteAllInBatch(expiredTickets);
            log.info("Completed cleanup for {} expired ticket selection hold(s)", expiredTickets.size());
        }
    }

    @Transactional
    public void cleanupExpiredBillHolds() {
        LocalDateTime threshold = LocalDateTime.now().minus(Constants.BILL_HOLD_DURATION);
        java.util.Optional<BillStatus> pendingStatus = findBillStatus(BillStatusEnum.PENDING);

        if (pendingStatus.isEmpty()) {
            log.warn("Pending bill status not found, skipping bill hold cleanup");
            return;
        }

        List<Bill> expiredBills = billRepo.findAllByBillStatusAndCreateTimeBeforeAndIsActiveTrue(
                pendingStatus.get(),
                threshold
        );

        if (!expiredBills.isEmpty()) {
            log.info("Found {} pending bill hold(s) to expire", expiredBills.size());
        }

        int expiredCount = 0;

        for (Bill bill : expiredBills) {
            try {
                selfProvider.getObject().expireBill(bill);
                expiredCount++;
            } catch (Exception ignored) {
                log.warn("Failed to cleanup expired ticket hold for bill id={}, tradingCode={}",
                        bill.getId(), bill.getTradingCode(), ignored);
            }
        }

        if (!expiredBills.isEmpty()) {
            log.info("Expired {}/{} pending bill hold(s)", expiredCount, expiredBills.size());
        }
    }

    public void cleanupExpiredTicketHolds() {
        cleanupExpiredBillHolds();
    }

    @Transactional
    public void deactivateCompletedSuccessBills() {
        java.util.Optional<BillStatus> successStatus = findBillStatus(BillStatusEnum.SUCCESS);

        if (successStatus.isEmpty()) {
            log.warn("Success bill status not found, skipping success bill deactivation");
            return;
        }

        List<Bill> completedBills = billRepo.findAllActiveSuccessBillsWithEndedShowtime(
                successStatus.get(),
                LocalDateTime.now()
        );

        if (!completedBills.isEmpty()) {
            log.info("Deactivating {} successful bill(s) with ended showtime", completedBills.size());
        }

        for (Bill bill : completedBills) {
            bill.setActive(false);
            bill.setUpdateTime(LocalDateTime.now());
        }

        if (!completedBills.isEmpty()) {
            billRepo.saveAll(completedBills);
            log.info("Deactivated {} successful bill(s) with ended showtime", completedBills.size());
        }
    }

    @Transactional
    public void expireBill(Bill bill) {
        releaseBillHold(bill, BillStatusEnum.EXPIRED);
    }

    @Transactional
    public void cancelBill(Bill bill) {
        releaseBillHold(bill, BillStatusEnum.CANCELLED);
    }

    private void releaseBillHold(Bill bill, BillStatusEnum nextStatus) {
        if (bill.getBillStatus() == null) {
            return;
        }
        if (!bill.isActive()) {
            return;
        }
        if (!isPendingStatus(bill.getBillStatus())) {
            return;
        }

        SeatStatus availableSeatStatus = seatStatusRepo.findByCode(SeatStatusEnum.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
        List<BillTicket> billTickets = billTicketRepo.findAllByBillId(bill.getId());
        List<Ticket> tickets = billTickets.stream()
                .map(BillTicket::getTicket)
                .filter(Objects::nonNull)
                .toList();

        if (!billTickets.isEmpty()) {
            seatRepo.updateSeatStatusByBill(bill, availableSeatStatus);
            billTicketRepo.deleteAllByBillId(bill.getId());
            ticketRepo.deleteAllInBatch(tickets);
        }

        bill.setBillStatus(getStatus(nextStatus));
        bill.setActive(false);
        bill.setUpdateTime(LocalDateTime.now());
        billRepo.save(bill);
    }

    private boolean isPendingStatus(BillStatus status) {
        if (status == null || status.getName() == null) {
            return false;
        }
        List<String> pendingNames = expectedDbNames(BillStatusEnum.PENDING);
        return pendingNames.stream()
                .map(this::normalize)
                .anyMatch(expected -> expected.equals(normalize(status.getName())));
    }

    public BillStatus getStatus(BillStatusEnum statusEnum) {
        return findBillStatus(statusEnum)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    private java.util.Optional<BillStatus> findBillStatus(BillStatusEnum statusEnum) {
        List<String> expectedNames = expectedDbNames(statusEnum);
        return billStatusRepo.findAll().stream()
                .filter(status -> status.getName() != null)
                .filter(status -> expectedNames.stream()
                        .map(this::normalize)
                        .anyMatch(expected -> expected.equals(normalize(status.getName()))))
                .findFirst();
    }

    private List<String> expectedDbNames(BillStatusEnum statusEnum) {
        List<String> names = new ArrayList<>();
        String name = statusEnum.name().toLowerCase();
        names.add(name);
        names.add(Character.toUpperCase(name.charAt(0)) + name.substring(1));

        if (statusEnum == BillStatusEnum.CANCELLED) {
            names.addAll(Set.of("cancel", "Cancel"));
        }
        if (statusEnum == BillStatusEnum.FAILURE) {
            names.addAll(Set.of("fail", "Fail"));
        }

        return names;
    }

    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", "").toLowerCase();
    }
}