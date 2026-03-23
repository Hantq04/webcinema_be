package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.seat.SeatType;

import java.util.List;

public interface SeatTypeService {
    SeatType insertSeatType(SeatType seatType);

    List<SeatType> getAllType();
}
