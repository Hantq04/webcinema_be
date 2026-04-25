package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.repository.bill.BillTicketRepo;
import vi.wbca.webcinema.repository.movie.TicketRepo;
import vi.wbca.webcinema.service.BillTicketService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillTicketServiceImpl implements BillTicketService {
    private final BillTicketRepo billTicketRepo;
    private final TicketRepo ticketRepo;

    @Override
    public void insertBillTicket(List<String> codes, Bill bill) {
        for (String code : codes) {
            Ticket ticket = ticketRepo.findByCode(code)
                    .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
            if (!ticket.isActive()) {
                throw new AppException(ErrorCode.TICKET_ALREADY_BOOKED);
            }

            BillTicket bt = new BillTicket();
            bt.setBill(bill);
            bt.setTicket(ticket);
            billTicketRepo.save(bt);
        }
    }

    @Override
    @Transactional
    public void updateBillTicket(List<String> codes, Bill bill) {
        Set<String> newCodes = new HashSet<>(codes);
        List<BillTicket> existing = billTicketRepo.findAllByBill(bill);

        Map<String, BillTicket> existingMap = existing.stream()
                .collect(Collectors.toMap(
                        bt -> bt.getTicket().getCode(),
                        bt -> bt
                ));
        Set<String> existingCodes = existingMap.keySet();

        Set<String> toDelete = new HashSet<>(existingCodes);
        toDelete.removeAll(newCodes);

        Set<String> toAdd = new HashSet<>(newCodes);
        toAdd.removeAll(existingCodes);

        if (!toDelete.isEmpty()) {
            List<BillTicket> deleteList = toDelete.stream()
                    .map(existingMap::get)
                    .toList();
            List<Ticket> ticketsToRelease = deleteList.stream()
                    .map(BillTicket::getTicket)
                    .peek(t -> t.setActive(true))
                    .toList();
            ticketRepo.saveAll(ticketsToRelease);
            billTicketRepo.deleteAll(deleteList);
        }

        if (!toAdd.isEmpty()) {
            List<Ticket> tickets = ticketRepo.findAllByCodeIn(toAdd);
            if (tickets.size() != toAdd.size()) {
                throw new AppException(ErrorCode.CODE_NOT_FOUND);
            }
            for (Ticket ticket : tickets) {
                if (!ticket.isActive()) {
                    throw new AppException(ErrorCode.TICKET_ALREADY_BOOKED);
                }
            }

            List<BillTicket> newBillTickets = tickets.stream()
                    .map(ticket -> {
                        BillTicket bt = new BillTicket();
                        bt.setBill(bill);
                        bt.setTicket(ticket);
                        return bt;
                    }).toList();
            billTicketRepo.saveAll(newBillTickets);
        }
    }

    @Override
    public void deleteBillTicket(Bill bill) {
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        billTicketRepo.deleteAll(billTickets);
    }

    @Override
    public void deleteTicket(Long id) {
        BillTicket billTicket = billTicketRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        billTicketRepo.delete(billTicket);
    }
}
