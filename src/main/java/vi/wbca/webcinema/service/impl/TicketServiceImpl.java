package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.ticket.TicketDTO;
import vi.wbca.webcinema.enums.SeatStatusEnum;
import vi.wbca.webcinema.enums.SeatTypeEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.TicketMapper;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.model.request.BookingRequest;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.repository.movie.TicketRepo;
import vi.wbca.webcinema.repository.seat.SeatRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.service.TicketService;
import vi.wbca.webcinema.service.ScheduleService;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepo ticketRepo;
    private final TicketMapper ticketMapper;
    private final ScheduleRepo scheduleRepo;
    private final SeatRepo seatRepo;
    private final RoomRepo roomRepo;
    private final SeatStatusRepo seatStatusRepo;
    private final GeneralSettingRepo generalSettingRepo;
    private final ScheduleService scheduleService;

    @Override
    public void insertTicket(BookingRequest request) {
        Room room = roomRepo.findByNameAndCode(
                request.getRoomName(), request.getRoomCode()
        ).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        Schedule schedule = getSchedule(request, room);

        // Check schedule
        updateTicket(schedule);

        List<Ticket> tickets = new ArrayList<>();
        for (String seatStr : request.getSeats()) {
            Seat seat = getSeat(seatStr, room, schedule);

            // Check seat belong room
            if (!seat.getRoom().getId().equals(room.getId())) {
                throw new AppException(ErrorCode.SEAT_NOT_BELONG_TO_ROOM);
            }
            double finalPrice = calculateFinalPrice(schedule, seat);

            Ticket ticket = new Ticket();
            ticket.setCode(GenerateCode.generateCode());
            ticket.setPriceTicket(finalPrice);
            ticket.setActive(true);
            ticket.setSchedule(schedule);
            ticket.setSeat(seat);
            tickets.add(ticket);
        }
        ticketRepo.saveAll(tickets);
    }

    @Override
    public void deleteTicket(String code) {
        Ticket ticket = ticketRepo.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        ticketRepo.delete(ticket);
    }

    public Schedule getSchedule(BookingRequest request, Room room) {
        return scheduleRepo.findByStartAtAndRoom(request.getStartTime(), room)
                .orElseThrow(() -> new AppException(ErrorCode.START_TIME_NOT_FOUND));
    }

    public Seat getSeat(String seatStr, Room room, Schedule schedule) {
        if (seatStr == null || seatStr.length() < 2) {
            throw new AppException(ErrorCode.INVALID_SEAT);
        }
        String line = seatStr.substring(0, 1);
        int number = Integer.parseInt(seatStr.substring(1));
        Seat seat = seatRepo.findByLineAndNumberAndRoom(line, number, room)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        boolean isAlreadyBooked = ticketRepo.existsByScheduleAndSeat(schedule, seat);
        if (isAlreadyBooked) {
            throw new AppException(ErrorCode.SEAT_OCCUPIED);
        }
        return seat;
    }

//    public Seat getSeat(TicketDTO ticketDTO, Room room) {
//        String selectSeat = ticketDTO.getSelectSeat();
//
//        if (selectSeat == null || selectSeat.length() < 2) {
//            throw new AppException(ErrorCode.INVALID_SEAT);
//        }
//        // Get line of seat
//        String line = selectSeat.substring(0, 1);
//        // Get number of seat
//        int number = Integer.parseInt(selectSeat.substring(1));
//
//        Seat seat = seatRepo.findByLineAndNumberAndRoom(line, number, room)
//                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
//
//        Schedule schedule = getSchedule(ticketDTO, room);
//
//        // Check seat status
//        boolean isAlreadyBooked = ticketRepo.existsByScheduleAndSeat(schedule, seat);
//        if (isAlreadyBooked) {
//            throw new AppException(ErrorCode.SEAT_OCCUPIED);
//        }
//
//        SeatStatus seatStatus = seatStatusRepo.findByCode(SeatStatusEnum.OCCUPIED.toString())
//                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
//        seat.setSeatStatus(seatStatus);
//        seatRepo.save(seat);
//
//        return seat;
//    }

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
        return SeatTypeEnum.getPriceByType(seatTypeName);
    }

    public GeneralSetting generalSetting() {
        return generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
    }

    public void updateTicket(Schedule schedule) {
        if (schedule.getEndAt().before(new Date())) {
            Ticket ticket = ticketRepo.findBySchedule(schedule);
            ticket.setActive(false);
            scheduleService.deactivateExpiredSchedule();
            throw new AppException(ErrorCode.SCHEDULE_EXPIRED);
        }
    }

    public void updateSeatStatusIfAvailable(Seat seat) {
        boolean hasActiveSchedules = scheduleRepo.existsByRoomAndStartAtBeforeAndEndAtAfter(
                seat.getRoom(), new Date(), new Date()
        );
        if (!hasActiveSchedules) {
            SeatStatus status = seatStatusRepo.findByCode(SeatStatusEnum.AVAILABLE.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
            seat.setSeatStatus(status);
            seatRepo.save(seat);
        }
    }
}
