package vi.wbca.webcinema.service;

import vi.wbca.webcinema.dto.room.SeatDTO;
import vi.wbca.webcinema.model.seat.Seat;

public interface SeatService {
    SeatDTO insertSeat(SeatDTO seatDTO);

    void updateSeat(SeatDTO seatDTO);

    void deleteSeat(Long id);

    Seat findById(Long id);

    void refreshSeat(String code);
}
