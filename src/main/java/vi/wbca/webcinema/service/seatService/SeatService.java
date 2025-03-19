package vi.wbca.webcinema.service.seatService;

import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.model.Seat;

public interface SeatService {
    SeatDTO insertSeat(SeatDTO seatDTO);

    void updateSeat(SeatDTO seatDTO);

    void deleteSeat(Long id);

    Seat findById(Long id);

    void refreshSeat(String code);
}
