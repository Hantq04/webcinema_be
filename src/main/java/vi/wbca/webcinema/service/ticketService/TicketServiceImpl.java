package vi.wbca.webcinema.service.ticketService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.TicketDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.TicketMapper;
import vi.wbca.webcinema.model.Schedule;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.Ticket;
import vi.wbca.webcinema.repository.ScheduleRepo;
import vi.wbca.webcinema.repository.SeatRepo;
import vi.wbca.webcinema.repository.TicketRepo;
import vi.wbca.webcinema.util.generate.GenerateCode;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService{
    private final TicketRepo ticketRepo;
    private final TicketMapper ticketMapper;
    private final ScheduleRepo scheduleRepo;
    private final SeatRepo seatRepo;

    @Override
    public void insertTicket(TicketDTO ticketDTO) {
        Ticket ticket = ticketMapper.toTicket(ticketDTO);

        ticket.setCode(GenerateCode.generateCode());
        ticket.setActive(true);
        ticket.setSchedule(getSchedule(ticketDTO));
        ticket.setSeat(getSeat(ticketDTO));
        ticketRepo.save(ticket);
    }

    public Schedule getSchedule(TicketDTO ticketDTO) {
        return scheduleRepo.findByStartAt(ticketDTO.getStartTime())
                .orElseThrow(() -> new AppException(ErrorCode.START_TIME_NOT_FOUND));
    }

    public Seat getSeat(TicketDTO ticketDTO) {
        String selectSeat = ticketDTO.getSelectSeat();

        if (selectSeat == null || selectSeat.length() < 2) {
            throw new AppException(ErrorCode.INVALID_SEAT);
        }
        // Get line of seat
        String line = selectSeat.substring(0, 1);
        // Get number of seat
        int number = Integer.parseInt(selectSeat.substring(1));

        return seatRepo.findByLineAndNumber(line, number)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
    }

    @Override
    public void deleteTicket(String code) {
        Ticket ticket = ticketRepo.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        ticketRepo.delete(ticket);
    }
}
