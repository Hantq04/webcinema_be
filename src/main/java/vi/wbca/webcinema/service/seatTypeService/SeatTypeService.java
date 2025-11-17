package vi.wbca.webcinema.service.seatTypeService;

import vi.wbca.webcinema.model.seat.SeatType;

import java.util.List;

public interface SeatTypeService {
    SeatType insertSeatType(SeatType seatType);

    List<SeatType> getAllType();
}
