package vi.wbca.webcinema.service.ticketService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.TicketDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.TicketMapper;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Schedule;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.Ticket;
import vi.wbca.webcinema.repository.RoomRepo;
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
    private final RoomRepo roomRepo;

    @Override
    public void insertTicket(TicketDTO ticketDTO) {
        Ticket ticket = ticketMapper.toTicket(ticketDTO);

        Room room = getRoom(ticketDTO);
        Schedule schedule = getSchedule(ticketDTO, room);
        Seat seat = getSeat(ticketDTO, room);

        // Check schedule and seat belong to room
        validateScheduleAndSeatInRoom(room, schedule, seat);

        ticket.setCode(GenerateCode.generateCode());
        ticket.setPriceTicket(schedule.getPrice());
        ticket.setActive(true);
        ticket.setSchedule(schedule);
        ticket.setSeat(seat);

        ticketDTO.setCode(ticket.getCode());
        ticketDTO.setPriceTicket(ticket.getPriceTicket());
        ticketRepo.save(ticket);
    }

    public Schedule getSchedule(TicketDTO ticketDTO, Room room) {
        return scheduleRepo.findByStartAtAndRoom(ticketDTO.getStartTime(), room)
                .orElseThrow(() -> new AppException(ErrorCode.START_TIME_NOT_FOUND));
    }

    public Seat getSeat(TicketDTO ticketDTO, Room room) {
        String selectSeat = ticketDTO.getSelectSeat();

        if (selectSeat == null || selectSeat.length() < 2) {
            throw new AppException(ErrorCode.INVALID_SEAT);
        }
        // Get line of seat
        String line = selectSeat.substring(0, 1);
        // Get number of seat
        int number = Integer.parseInt(selectSeat.substring(1));

        return seatRepo.findByLineAndNumberAndRoom(line, number, room)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
    }

    public Room getRoom(TicketDTO ticketDTO) {
        return roomRepo.findByNameAndCode(ticketDTO.getRoomName(), ticketDTO.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    public void validateScheduleAndSeatInRoom(Room room, Schedule schedule, Seat seat) {
        if (!schedule.getRoom().getId().equals(room.getId())) {
            throw new AppException(ErrorCode.SCHEDULE_NOT_BELONG_TO_ROOM);
        }
        if (!seat.getRoom().getId().equals(room.getId())) {
            throw new AppException(ErrorCode.SEAT_NOT_BELONG_TO_ROOM);
        }
    }

    @Override
    public void deleteTicket(String code) {
        Ticket ticket = ticketRepo.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        ticketRepo.delete(ticket);
    }
}
