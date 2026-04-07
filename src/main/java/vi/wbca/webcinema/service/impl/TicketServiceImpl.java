package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.RoomTypeEnum;
import vi.wbca.webcinema.enums.SeatTypeEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.model.request.BookingRequest;
import vi.wbca.webcinema.model.response.BookingResponse;
import vi.wbca.webcinema.model.response.TicketResponse;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.repository.movie.TicketRepo;
import vi.wbca.webcinema.repository.seat.SeatRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.service.SeatService;
import vi.wbca.webcinema.service.TicketService;
import vi.wbca.webcinema.service.ScheduleService;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepo ticketRepo;
    private final ScheduleRepo scheduleRepo;
    private final SeatRepo seatRepo;
    private final RoomRepo roomRepo;
    private final GeneralSettingRepo generalSettingRepo;
    private final ScheduleService scheduleService;
    private final SeatService seatService;

    @Override
    public BookingResponse insertTicket(BookingRequest request) {
        Room room = roomRepo.findByNameAndCode(request.getRoomName(), request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        Schedule schedule = getSchedule(request, room);

        updateTicket(schedule);

        List<Seat> seats = request.getSeats().stream()
                .map(seatStr -> parseSeat(seatStr, room))
                .toList();

        seatService.validateSeatSelection(seats);

        for (Seat seat : seats) {
            boolean isAlreadyBooked = ticketRepo.existsByScheduleAndSeat(schedule, seat);
            if (isAlreadyBooked) {
                throw new AppException(ErrorCode.SEAT_OCCUPIED);
            }
        }

        List<Ticket> tickets = new ArrayList<>();
        for (Seat seat : seats) {
            Long finalPrice = calculateFinalPrice(schedule, seat);

            Ticket ticket = new Ticket();
            ticket.setCode(GenerateCode.generateCode());
            ticket.setPriceTicket(finalPrice);
            ticket.setActive(true);
            ticket.setSchedule(schedule);
            ticket.setSeat(seat);
            tickets.add(ticket);
        }
        ticketRepo.saveAll(tickets);

        List<String> ticketCodes = tickets.stream()
                .map(Ticket::getCode)
                .toList();
        return BookingResponse.builder()
                .ticketCodes(ticketCodes)
                .build();
    }

    @Override
    public void deleteTicket(String code) {
        Ticket ticket = ticketRepo.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        ticketRepo.delete(ticket);
    }

    @Override
    public List<TicketResponse> getAllTicket() {
        return ticketRepo.findAll().stream().map(ticket -> new TicketResponse(
                ticket.getId(),
                ticket.getCode(),
                ticket.isActive(),
                ticket.getPriceTicket()
        )).toList();
    }

    public Schedule getSchedule(BookingRequest request, Room room) {
        return scheduleRepo.findByStartAtAndRoom(request.getStartTime(), room)
                .orElseThrow(() -> new AppException(ErrorCode.START_TIME_NOT_FOUND));
    }

    public Long calculateFinalPrice(Schedule schedule, Seat seat) {
        GeneralSetting setting = generalSetting();
        double basePrice = getSeatPrice(seat);
        double roomMultiplier = getRoomPriceMultiplier(schedule);

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

        DayOfWeek dayOfWeek = schedule.getStartAt().getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

        double finalPrice = basePrice * roomMultiplier;
        finalPrice *= (isWeekend ? (1 + setting.getPercentWeekend() / 100.0) : 1);
        finalPrice *= (1 - discount);

        return Math.round(finalPrice);
    }

    public double getRoomPriceMultiplier(Schedule schedule) {
        Room room = schedule.getRoom();
        if (room == null || room.getType() == null) {
            throw new AppException(ErrorCode.TYPE_NOT_FOUND);
        }
        RoomTypeEnum roomType = room.getType();
        return roomType.getPriceMultiplier();
    }

    public Double getSeatPrice(Seat seat) {
        int seatTypeId = Math.toIntExact(seat.getSeatType().getId());
        String seatTypeName = switch (seatTypeId) {
            case 1 -> "STANDARD";
            case 2 -> "VIP";
            case 3 -> "SWEET_BOX";
            default -> throw new AppException(ErrorCode.INVALID_SEAT);
        };
        return SeatTypeEnum.getPriceByType(seatTypeName);
    }

    public GeneralSetting generalSetting() {
        return generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
    }

    public void updateTicket(Schedule schedule) {
        if (schedule.getEndAt().isBefore(LocalDateTime.now())) {
            Ticket ticket = ticketRepo.findBySchedule(schedule);
            ticket.setActive(false);
            scheduleService.deactivateExpiredSchedule();
            throw new AppException(ErrorCode.SCHEDULE_EXPIRED);
        }
    }

    public Seat parseSeat(String seatStr, Room room) {
        if (seatStr == null || seatStr.length() < 2) {
            throw new AppException(ErrorCode.INVALID_SEAT);
        }
        String line = seatStr.substring(0, 1);
        int number = Integer.parseInt(seatStr.substring(1));
        return seatRepo.findByLineAndNumberAndRoom(line, number, room)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
    }
}
