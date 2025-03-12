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
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.BillRepo;
import vi.wbca.webcinema.repository.BillTicketRepo;
import vi.wbca.webcinema.repository.TicketRepo;
import vi.wbca.webcinema.repository.UserRepo;

@Service
@RequiredArgsConstructor
public class BillTicketServiceImpl implements BillTicketService{
    private final BillTicketRepo billTicketRepo;
    private final BillTicketMapper billTicketMapper;
    private final TicketRepo ticketRepo;
    private final BillRepo billRepo;
    private final UserRepo userRepo;

    @Override
    public void insertBillTicket(BillTicketDTO billTicketDTO) {
        BillTicket billTicket = billTicketMapper.toBillTicket(billTicketDTO);
        Bill bill = getBill(billTicketDTO);
        Ticket ticket = getTicket(billTicketDTO);

        billTicket.setBill(bill);
        billTicket.setTicket(ticket);
        billTicketRepo.save(billTicket);
    }

    public Bill getBill(BillTicketDTO billTicketDTO) {
        User user = userRepo.findByUserName(billTicketDTO.getCustomerName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        return billRepo.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
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
