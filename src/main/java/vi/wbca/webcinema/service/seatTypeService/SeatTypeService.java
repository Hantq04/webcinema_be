package vi.wbca.webcinema.service.seatTypeService;

import vi.wbca.webcinema.model.SeatType;

import java.util.List;

public interface SeatTypeService {
    SeatType insertSeatType(SeatType seatType);

    List<SeatType> getAllType();
}
