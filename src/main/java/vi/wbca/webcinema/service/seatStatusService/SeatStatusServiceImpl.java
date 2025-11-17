package vi.wbca.webcinema.service.seatStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.seat.SeatStatus;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatStatusServiceImpl implements SeatStatusService {
    private final SeatStatusRepo seatStatusRepo;

    @Override
    public SeatStatus insertSeatStatus(SeatStatus seatStatus) {
        return seatStatusRepo.save(seatStatus);
    }

    @Override
    public List<SeatStatus> getAllStatus() {
        return seatStatusRepo.findAll();
    }
}
