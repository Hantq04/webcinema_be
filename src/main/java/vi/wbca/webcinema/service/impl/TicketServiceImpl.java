package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.request.BookingRequest;
import vi.wbca.webcinema.model.response.BookingResponse;
import vi.wbca.webcinema.model.response.TicketResponse;
import vi.wbca.webcinema.mapper.TicketMapper;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.repository.movie.TicketRepo;
import vi.wbca.webcinema.repository.seat.SeatRepo;
import vi.wbca.webcinema.service.SeatService;
import vi.wbca.webcinema.service.TicketPricingService;
import vi.wbca.webcinema.service.TicketService;
import vi.wbca.webcinema.service.ScheduleService;
import vi.wbca.webcinema.util.generate.GenerateCode;

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
    private final ScheduleService scheduleService;
    private final SeatService seatService;
    private final TicketPricingService ticketPricingService;
    private final TicketMapper ticketMapper;

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
            Long finalPrice = ticketPricingService.calculateFinalPrice(schedule, seat.getSeatType().getNameType());

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
        return ticketRepo.findAll().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }

    public Schedule getSchedule(BookingRequest request, Room room) {
        return scheduleRepo.findByStartAtAndRoom(request.getStartTime(), room)
                .orElseThrow(() -> new AppException(ErrorCode.START_TIME_NOT_FOUND));
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
