package vi.wbca.webcinema.service.billTicketService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.BillTicketDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillTicketMapper;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillTicket;
import vi.wbca.webcinema.model.Ticket;
import vi.wbca.webcinema.repository.BillTicketRepo;
import vi.wbca.webcinema.repository.TicketRepo;

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

        ticket.setActive(true);
        ticketRepo.save(ticket);
        billTicket.setBill(bill);
        billTicket.setTicket(ticket);

        billTicketRepo.save(billTicket);
    }

    public Ticket getTicket(BillTicketDTO billTicketDTO) {
        return ticketRepo.findByCode(billTicketDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }

    @Override
    public void deleteBillTicket(Long id) {
        BillTicket billTicket = billTicketRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        billTicketRepo.delete(billTicket);
    }
}
