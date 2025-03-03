package vi.wbca.webcinema.service.seatService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.SeatMapper;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.repository.SeatRepo;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService{
    private final SeatRepo seatRepo;
    private final SeatMapper seatMapper;

    @Override
    public SeatDTO insertSeat(SeatDTO request) {
        Seat seat = seatMapper.toSeat(request);
        seat.setActive(true);
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
