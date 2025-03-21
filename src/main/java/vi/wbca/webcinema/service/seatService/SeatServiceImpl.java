package vi.wbca.webcinema.service.seatService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.dto.room.SeatDTO;
import vi.wbca.webcinema.enums.ESeatStatus;
import vi.wbca.webcinema.enums.ESeatType;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.SeatMapper;
import vi.wbca.webcinema.model.*;
import vi.wbca.webcinema.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService{
    private final SeatRepo seatRepo;
    private final SeatMapper seatMapper;
    private final SeatStatusRepo seatStatusRepo;
    private final RoomRepo roomRepo;
    private final SeatTypeRepo seatTypeRepo;
    private final BillRepo billRepo;
    private final BillTicketRepo billTicketRepo;

    @Override
    public SeatDTO insertSeat(SeatDTO request) {
        Seat seat = seatMapper.toSeat(request);
        Room room = roomRepo.findByNameAndCode(request.getRoomName(), request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (seatRepo.existsByRoomAndLineAndNumber(room, request.getLine(), request.getNumber()) ) {
            throw new AppException(ErrorCode.SEAT_EXISTED);
        }
        seat.setActive(true);
        seat.setRoom(room);
        seat.setSeatStatus(getSeatStatus());

        request.setRoomName(room.getName());
        request.setRoomCode(room.getCode());
        seatRepo.save(seat);

        setSeatType(seat, request.getLine());
        return seatMapper.toSeatDTO(seat);
    }

    public void setSeatType(Seat seat, String line) {
        int rowNumber = line.charAt(0) - 'A' + 1;
        if (rowNumber >= 1 && rowNumber <= 4) {
            SeatType seatType = seatTypeRepo.findByNameType(ESeatType.STANDARD.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            seat.setSeatType(seatType);
        } else if (rowNumber >= 5 && rowNumber < 22) {
            SeatType seatType = seatTypeRepo.findByNameType(ESeatType.VIP.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            seat.setSeatType(seatType);
        } else {
            SeatType seatType = seatTypeRepo.findByNameType(ESeatType.DELUXE.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            seat.setSeatType(seatType);
        }
        seatRepo.save(seat);
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

    public SeatStatus getSeatStatus() {
        return seatStatusRepo.findByCode(ESeatStatus.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
    }

    @Override
    public Seat findById(Long id) {
        return seatRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public Bill getBill(String code) {
        return billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }

    public void validatedBillTicket(Bill bill) {
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        if (billTickets.isEmpty()) throw new AppException(ErrorCode.CODE_NOT_FOUND);
    }

    @Override
    @Transactional
    public void refreshSeat(String code) {
        Bill bill = getBill(code);
        Long statusCode = bill.getBillStatus().getId();
        if (statusCode == 2) return;

        SeatStatus seatStatus = getSeatStatus();
        validatedBillTicket(bill);

        seatRepo.updateSeatStatusByBill(bill, seatStatus);
    }
}
