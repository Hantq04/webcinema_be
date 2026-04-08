package vi.wbca.webcinema.service;

import org.springframework.beans.factory.ObjectProvider;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public void cleanupExpiredTicketHolds() {
        BillStatus pendingStatus = getBillStatus(BillStatusEnum.PENDING.toString());
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Bill> expiredBills = billRepo.findAllByBillStatusAndCreateTimeBeforeAndIsActiveTrue(
                pendingStatus,
                threshold
        );

        for (Bill bill : expiredBills) {
            try {
                selfProvider.getObject().expireBill(bill);
            } catch (Exception ignored) {
                // Keep scanning other expired holds even if one cleanup fails.
            }
        }
    }

    @Transactional
    public void expireBill(Bill bill) {
        releaseBillHold(bill, BillStatusEnum.EXPIRED.toString());
    }

    @Transactional
    public void cancelBill(Bill bill) {
        releaseBillHold(bill, BillStatusEnum.CANCELLED.toString());
    }

    private void releaseBillHold(Bill bill, String nextStatus) {
        if (bill.getBillStatus() == null || !BillStatusEnum.PENDING.toString().equalsIgnoreCase(bill.getBillStatus().getName())) {
            return;
        }

        SeatStatus availableSeatStatus = seatStatusRepo.findByCode(SeatStatusEnum.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        List<Ticket> tickets = billTickets.stream()
                .map(BillTicket::getTicket)
                .filter(Objects::nonNull)
                .toList();

        if (!billTickets.isEmpty()) {
            seatRepo.updateSeatStatusByBill(bill, availableSeatStatus);
            billTicketRepo.deleteAll(billTickets);
        }

        if (!tickets.isEmpty()) {
            ticketRepo.deleteAll(tickets);
        }

        bill.setBillStatus(getBillStatus(nextStatus));
        bill.setActive(false);
        bill.setUpdateTime(LocalDateTime.now());
        billRepo.save(bill);
    }

    private BillStatus getBillStatus(String name) {
        return billStatusRepo.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }
}