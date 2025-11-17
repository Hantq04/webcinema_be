package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.seat.SeatStatus;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.service.SeatStatusService;

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
