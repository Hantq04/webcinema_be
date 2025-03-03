package vi.wbca.webcinema.service.seatService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.enums.ESeatStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.SeatMapper;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.SeatStatus;
import vi.wbca.webcinema.repository.RoomRepo;
import vi.wbca.webcinema.repository.SeatRepo;
import vi.wbca.webcinema.repository.SeatStatusRepo;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService{
    private final SeatRepo seatRepo;
    private final SeatMapper seatMapper;
    private final SeatStatusRepo seatStatusRepo;
    private final RoomRepo roomRepo;

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
        return seatMapper.toSeatDTO(seat);
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
