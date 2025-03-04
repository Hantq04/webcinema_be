package vi.wbca.webcinema.service.seatService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.enums.ESeatStatus;
import vi.wbca.webcinema.enums.ESeatType;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.SeatMapper;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.SeatStatus;
import vi.wbca.webcinema.model.SeatType;
import vi.wbca.webcinema.repository.RoomRepo;
import vi.wbca.webcinema.repository.SeatRepo;
import vi.wbca.webcinema.repository.SeatStatusRepo;
import vi.wbca.webcinema.repository.SeatTypeRepo;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService{
    private final SeatRepo seatRepo;
    private final SeatMapper seatMapper;
    private final SeatStatusRepo seatStatusRepo;
    private final RoomRepo roomRepo;
    private final SeatTypeRepo seatTypeRepo;

    @Override
    public SeatDTO insertSeat(SeatDTO request) {
        Seat seat = seatMapper.toSeat(request);
        SeatStatus seatStatus = seatStatusRepo.findByCode(ESeatStatus.AVAILABLE.toString())
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
        Room room = roomRepo.findByCode(request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        seat.setActive(true);
        seat.setRoom(room);
        seat.setSeatStatus(seatStatus);
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

    @Override
    public Seat findById(Long id) {
        return seatRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }
}
