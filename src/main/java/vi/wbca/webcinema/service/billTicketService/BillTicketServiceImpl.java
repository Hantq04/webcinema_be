package vi.wbca.webcinema.service.billTicketService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.bill.BillTicketDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillTicketMapper;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillTicket;
import vi.wbca.webcinema.model.Ticket;
import vi.wbca.webcinema.repository.BillTicketRepo;
import vi.wbca.webcinema.repository.TicketRepo;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BillTicketServiceImpl implements BillTicketService{
    private final BillTicketRepo billTicketRepo;
    private final BillTicketMapper billTicketMapper;
    private final TicketRepo ticketRepo;

    @Override
    public void insertBillTicket(BillTicketDTO billTicketDTO, Bill bill) {
        BillTicket billTicket = billTicketMapper.toBillTicket(billTicketDTO);
        Ticket ticket = getTicket(billTicketDTO);

        ticket.setActive(false);
        ticketRepo.save(ticket);
        billTicket.setQuantity(1);
        billTicket.setBill(bill);
        billTicket.setTicket(ticket);

        billTicketRepo.save(billTicket);
    }

    public Ticket getTicket(BillTicketDTO billTicketDTO) {
        return ticketRepo.findByCode(billTicketDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }

    @Override
    public void updateBillTicket(List<BillTicketDTO> billTicketDTOs, Bill bill) {
        // Retrieve existing BillTicket records from the database
        List<BillTicket> existingBillTickets = billTicketRepo.findAllByBill(bill);

        // Create a list to store processed foods to keep track of updated/added tickets
        List<Long> processedId = billTicketDTOs.stream()
                .map(BillTicketDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        for (BillTicketDTO billTicketDTO : billTicketDTOs) {
            Ticket ticket = getTicket(billTicketDTO);
            BillTicket billTicket;

            if (billTicketDTO.getId() != null) {
                billTicket = billTicketRepo.findById(billTicketDTO.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));

                billTicket.setTicket(ticket);
            } else {
                 billTicket = billTicketMapper.toBillTicket(billTicketDTO);

                 billTicket.setQuantity(1);
                 billTicket.setTicket(ticket);
                 billTicket.setBill(bill);
            }

            billTicketRepo.save(billTicket);
        }

        // Delete BillTicket records from DB that are not included in the new DTO list
        for (BillTicket oldBillTicket : existingBillTickets) {
            if (!processedId.contains(oldBillTicket.getId())) {
                billTicketRepo.delete(oldBillTicket);
            }
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
