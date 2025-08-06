package vi.wbca.webcinema.service.ticketService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.ticket.TicketDTO;
import vi.wbca.webcinema.enums.ESeatStatus;
import vi.wbca.webcinema.enums.ESeatType;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.TicketMapper;
import vi.wbca.webcinema.model.*;
import vi.wbca.webcinema.repository.*;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService{
    private final TicketRepo ticketRepo;
    private final TicketMapper ticketMapper;
    private final ScheduleRepo scheduleRepo;
    private final SeatRepo seatRepo;
    private final RoomRepo roomRepo;
    private final SeatStatusRepo seatStatusRepo;
    private final GeneralSettingRepo generalSettingRepo;

    @Override
    public void insertTicket(TicketDTO ticketDTO) {
        Ticket ticket = ticketMapper.toTicket(ticketDTO);

        Room room = getRoom(ticketDTO);
        Schedule schedule = getSchedule(ticketDTO, room);

        if (schedule.getEndAt().before(new Date())) {
            throw new AppException(ErrorCode.SCHEDULE_EXPIRED);
        }

        // Check seat availability in the room
        checkSeatAvailable(room, schedule);
        Seat seat = getSeat(ticketDTO, room);

        // Check schedule and seat belong to room
        validateScheduleAndSeatInRoom(room, schedule, seat);
        double finalPrice = calculateFinalPrice(schedule, seat);

        ticket.setCode(GenerateCode.generateCode());
        ticket.setPriceTicket(finalPrice);
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

        Seat seat = seatRepo.findByLineAndNumberAndRoom(line, number, room)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        SeatStatus seatStatus = seatStatusRepo.findByCode(ESeatStatus.OCCUPIED.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));

        // Check seat available or occupied
        if (seat.getSeatStatus().equals(seatStatus)) {
            throw new AppException(ErrorCode.SEAT_OCCUPIED);
        }

        seat.setSeatStatus(seatStatus);
        seatRepo.save(seat);
        return seat;
    }

    public void checkSeatAvailable(Room room, Schedule schedule) {
        int totalSeats = seatRepo.countByRoom(room);
        int bookedSeats = ticketRepo.countBySchedule(schedule);

        if (bookedSeats >= totalSeats) {
            throw new AppException(ErrorCode.ROOM_FULL);
        }
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

    public Double calculateFinalPrice(Schedule schedule, Seat seat) {
        GeneralSetting setting = generalSetting();
        double basePrice = getSeatPrice(seat);

        String showTimeName = schedule.getName();
        double discount = switch (showTimeName) {
            // The price is discounted based on the showtime
            case "MORNING" -> 0.15;
            case "NOON" -> 0.10;
            case "AFTERNOON" -> 0.05;
            case "EVENING" -> 0.0;
            case "LATE_NIGHT" -> 0.20;
            default -> throw new AppException(ErrorCode.INVALID_SHOW_TIME);
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(schedule.getStartAt());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

        double finalPrice = basePrice * (isWeekend ?
                (1 + setting.getPercentWeekend() / 100.0) : 1);
        finalPrice *= (1 - discount);

        return finalPrice;
    }

    public Double getSeatPrice(Seat seat) {
        int seatTypeId = Math.toIntExact(seat.getSeatType().getId());
        String seatTypeName = switch (seatTypeId) {
            case 1 -> "STANDARD";
            case 2 -> "VIP";
            case 3 -> "DELUXE";
            default -> throw new AppException(ErrorCode.INVALID_SEAT);
        };
        return ESeatType.getPriceByType(seatTypeName);
    }

    public GeneralSetting generalSetting() {
        return generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
    }

    @Override
    public void deleteTicket(String code) {
        Ticket ticket = ticketRepo.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        ticketRepo.delete(ticket);
    }
}
