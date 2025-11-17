package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.seat.SeatType;
import vi.wbca.webcinema.repository.seat.SeatTypeRepo;
import vi.wbca.webcinema.service.SeatTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatTypeServiceImpl implements SeatTypeService {
    private final SeatTypeRepo seatTypeRepo;

    @Override
    public SeatType insertSeatType(SeatType seatType) {
        return seatTypeRepo.save(seatType);
    }

    @Override
    public List<SeatType> getAllType() {
        return seatTypeRepo.findAll();
    }
}
