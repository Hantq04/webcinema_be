package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.bill.BillDTO;
import vi.wbca.webcinema.model.dto.cinema.CinemaRevenueDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BillService {
    void createBill(BillDTO billDTO);

    void updateBill(BillDTO billDTO);

    void deleteBill(String code);

    List<CinemaRevenueDTO> getRevenueByCinema(LocalDateTime from, LocalDateTime to);
}
