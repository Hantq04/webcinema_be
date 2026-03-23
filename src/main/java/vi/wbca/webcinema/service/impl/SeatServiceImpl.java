package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.model.dto.room.SeatDTO;
import vi.wbca.webcinema.enums.SeatStatusEnum;
import vi.wbca.webcinema.enums.SeatTypeEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.model.entity.seat.SeatType;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.BillTicketRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.seat.SeatRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.repository.seat.SeatTypeRepo;
import vi.wbca.webcinema.service.SeatService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepo seatRepo;
    private final SeatStatusRepo seatStatusRepo;
    private final RoomRepo roomRepo;
    private final SeatTypeRepo seatTypeRepo;
    private final BillRepo billRepo;
    private final BillTicketRepo billTicketRepo;

    @Override
    public SeatDTO insertSeat(SeatDTO request) {
        Room room = roomRepo.findByNameAndCode(request.getRoomName(), request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        // Check if the room already created seat
        boolean hasSeats  = seatRepo.existsByRoom(room);
        if (hasSeats) {
            throw new AppException(ErrorCode.SEAT_EXISTED);
        }
        // Generate seats for the room if not already exists
        generateSeatsForRoom(room);
        request.setTotalSeats(room.getCapacity());
        request.setRoomName(room.getName());
        request.setRoomCode(room.getCode());
        return request;
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
        Seat seat = findById(id);
        seatRepo.delete(seat);
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
        Long statusCode = bill.getBillStatus().getId();
        if (statusCode == 2) return;

        SeatStatus seatStatus = getSeatStatus();
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        if (billTickets.isEmpty()) throw new AppException(ErrorCode.CODE_NOT_FOUND);
        seatRepo.updateSeatStatusByBill(bill, seatStatus);
    }

    @Override
    public List<Seat> getAllSeat() {
        return seatRepo.findAll();
    }

    public void generateSeatsForRoom(Room room) {
        int capacity = room.getCapacity();
        int roomLine = (int) Math.ceil(Math.sqrt(capacity));
        int seatsPerRow = (int) Math.ceil((double) capacity / roomLine);
        List<Seat> seats = new ArrayList<>();

        for (int i = 0; i < roomLine; i++) {
            char rowLabel = (char) ('A' + i);
            for (int j = 1; j <= seatsPerRow && seats.size() < capacity; j++) {
                // Check if the seat already exists
                if (seatRepo.existsByRoomAndLineAndNumber(room, String.valueOf(rowLabel), j)) {
                    continue;// Skip if it already exists
                }
                Seat seat = new Seat();
                seat.setLine(String.valueOf(rowLabel));
                seat.setNumber(j);
                seat.setRoom(room);
                seat.setSeatStatus(getSeatStatus());
                setSeatType(seat, String.valueOf(rowLabel));
                seat.setActive(true);
                seats.add(seat);
            }
        }
        if (!seats.isEmpty()) {
            // Save only if new seats are available
            seatRepo.saveAll(seats);
        }
    }

    public void setSeatType(Seat seat, String line) {
        int rowNumber = line.charAt(0) - 'A' + 1;
        if (rowNumber >= 1 && rowNumber <= 4) {
            SeatType seatType = seatTypeRepo.findByNameType(SeatTypeEnum.STANDARD.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            seat.setSeatType(seatType);
        } else if (rowNumber >= 5 && rowNumber < 17) {
            SeatType seatType = seatTypeRepo.findByNameType(SeatTypeEnum.VIP.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            seat.setSeatType(seatType);
        } else {
            SeatType seatType = seatTypeRepo.findByNameType(SeatTypeEnum.DELUXE.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            seat.setSeatType(seatType);
        }
        seatRepo.save(seat);
    }

    public SeatStatus getSeatStatus() {
        return seatStatusRepo.findByCode(SeatStatusEnum.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
    }
}
