package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.model.dto.room.SeatByScheduleDTO;
import vi.wbca.webcinema.model.dto.room.SeatDTO;
import vi.wbca.webcinema.enums.SeatStatusEnum;
import vi.wbca.webcinema.enums.SeatTypeEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.model.entity.seat.SeatType;
import vi.wbca.webcinema.model.response.SeatResponse;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.BillTicketRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.repository.seat.SeatRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.repository.seat.SeatTypeRepo;
import vi.wbca.webcinema.service.SeatService;
import vi.wbca.webcinema.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepo seatRepo;
    private final SeatStatusRepo seatStatusRepo;
    private final RoomRepo roomRepo;
    private final SeatTypeRepo seatTypeRepo;
    private final BillRepo billRepo;
    private final BillTicketRepo billTicketRepo;
    private final ScheduleRepo scheduleRepo;

    @Override
    public void insertSeat(SeatDTO request) {
        Room room = roomRepo.findByNameAndCode(request.getRoomName(), request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (seatRepo.existsByRoom(room)) {
            throw new AppException(ErrorCode.SEAT_EXISTED);
        }

        generateSeatsForRoom(room);
        request.setTotalSeats(room.getCapacity());
        request.setRoomName(room.getName());
        request.setRoomCode(room.getCode());
    }

    @Override
    public void updateSeat(SeatDTO request) {
        Seat seat = findById(request.getId());
        seat.setLine(request.getLine());
        seat.setNumber(request.getNumber());
        seatRepo.save(seat);
    }

    @Override
    public void deleteSeat(Long id) {
        seatRepo.delete(findById(id));
    }

    @Override
    public Seat findById(Long id) {
        return seatRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public void refreshSeat(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        if (bill.getBillStatus().getId() == 2) return;
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        if (billTickets.isEmpty()) {
            throw new AppException(ErrorCode.CODE_NOT_FOUND);
        }

        seatRepo.updateSeatStatusByBill(bill, getSeatStatus());
    }

    @Override
    public List<SeatResponse> getAllSeat() {
        return seatRepo.findAll().stream().map(seat -> new SeatResponse(
                seat.getId(),
                seat.getLine(),
                seat.getNumber(),
                seat.getSeatStatus().getNameStatus(),
                seat.getRoom().getCode(),
                seat.getSeatType().getNameType()
        )).toList();
    }

    @Override
    public void validateSeatSelection(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            throw new AppException(ErrorCode.SEAT_EMPTY);
        }
        Set<String> types = seats.stream()
                .map(s -> s.getSeatType().getNameType())
                .collect(Collectors.toSet());
        if (types.size() > 1) {
            throw new AppException(ErrorCode.SEAT_TYPE_NOT_MATCH);
        }

        SeatTypeEnum type = SeatTypeEnum.getByName(types.iterator().next());
        if (type == SeatTypeEnum.SWEET_BOX) {
            Seat s1 = seats.get(0);
            Seat s2 = seats.get(1);

            if (seats.size() != 2) {
                throw new AppException(ErrorCode.SWEET_BOX_MUST_BE_PAIR);
            }
            if (!s1.getLine().equals(s2.getLine())) {
                throw new AppException(ErrorCode.SEAT_NOT_SAME_ROW);
            }
            if (Math.abs(s1.getNumber() - s2.getNumber()) != 1) {
                throw new AppException(ErrorCode.SEAT_NOT_ADJACENT);
            }
            if (!s1.getPairIndex().equals(s2.getPairIndex())) {
                throw new AppException(ErrorCode.INVALID_SWEET_BOX_PAIR);
            }
        }
    }

    @Override
    public Map<String, Object> getSeatBySchedule(String scheduleCode) {
        Schedule schedule = scheduleRepo.findByCode(scheduleCode)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        
        Room room = schedule.getRoom();
        List<SeatByScheduleDTO> seatsDto = seatRepo.getSeatsWithStatusBySchedule(scheduleCode);
        Integer bookedSeats = seatRepo.countBookedSeatsBySchedule(scheduleCode);
        Integer capacity = room.getCapacity();
        Integer remainSeats = capacity - (bookedSeats != null ? bookedSeats : 0);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cinema", room.getName());
        response.put("room", room.getCode());
        response.put("remainSeats", remainSeats);
        response.put("capacity", capacity);
        response.put("startAt", schedule.getStartAt().format(Constants.DATE_TIME_FORMATTER));
        response.put("endAt", schedule.getEndAt().format(Constants.DATE_TIME_FORMATTER));
        response.put("seats", seatsDto);
        
        return response;
    }

    @Transactional
    public void generateSeatsForRoom(Room room) {
        int capacity = room.getCapacity();
        int totalRows = Math.max(5, (int) Math.ceil(capacity / 12.0));
        int baseSeatsPerRow = capacity / totalRows;
        int extraSeats = capacity % totalRows;
        SeatType standard = getSeatType(SeatTypeEnum.STANDARD);
        SeatType vip = getSeatType(SeatTypeEnum.VIP);
        SeatType sweetBox = getSeatType(SeatTypeEnum.SWEET_BOX);
        SeatStatus status = getSeatStatus();
        int standardRows = Math.max(1, (int) Math.ceil(totalRows * 0.3));

        Set<String> existingSeatSet = seatRepo.findByRoom(room).stream()
                .map(seat -> seat.getLine() + "-" + seat.getNumber())
                .collect(Collectors.toSet());

        int lastRowSeats = baseSeatsPerRow + (extraSeats > 0 ? 1 : 0);
        if (lastRowSeats % 2 != 0) {
            lastRowSeats++;
            if (extraSeats > 0) extraSeats--;
            else baseSeatsPerRow--;
        }

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < totalRows; i++) {
            char rowLabel = (char) ('A' + i);
            int seatsInRow = (i == totalRows - 1) ? lastRowSeats
                    : baseSeatsPerRow + ((totalRows - 2 - i) < extraSeats ? 1 : 0);
            int pairIndex = 1;

            for (int j = 1; j <= seatsInRow; j++) {
                String key = rowLabel + "-" + j;
                if (existingSeatSet.contains(key)) continue;

                Seat seat = new Seat();
                seat.setLine(String.valueOf(rowLabel));
                seat.setNumber(j);
                seat.setRoom(room);
                seat.setSeatStatus(status);
                seat.setActive(true);

                setSeatType(seat, i, totalRows, standardRows, standard, vip, sweetBox);

                if (i == totalRows - 1) {
                    seat.setPairIndex(pairIndex);
                    if (j % 2 == 0) pairIndex++;
                }
                seats.add(seat);
            }
        }

        if (!seats.isEmpty()) {
            seatRepo.saveAll(seats);
        }
    }

    private void setSeatType(Seat seat, int rowIndex, int totalRows, int standardRows,
                             SeatType standard, SeatType vip, SeatType sweetBox) {

        if (rowIndex == totalRows - 1) {
            seat.setSeatType(sweetBox);
        } else if (rowIndex < standardRows) {
            seat.setSeatType(standard);
        } else {
            seat.setSeatType(vip);
        }
    }

    private SeatType getSeatType(SeatTypeEnum type) {
        return seatTypeRepo.findByNameType(type.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public SeatStatus getSeatStatus() {
        return seatStatusRepo.findByCode(SeatStatusEnum.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
    }
}