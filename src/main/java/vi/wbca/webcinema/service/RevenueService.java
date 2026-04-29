package vi.wbca.webcinema.service;

import vi.wbca.webcinema.enums.RevenueGroupByEnum;
import vi.wbca.webcinema.model.dto.revenue.RevenueTimePointDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueService {
    List<RevenueTimePointDTO> getRevenueSummary(LocalDateTime from,
                                                LocalDateTime to,
                                                RevenueGroupByEnum groupBy,
                                                Long cinemaId,
                                                Long roomId,
                                                Long movieId);

    byte[] exportRevenueSummary(LocalDateTime from,
                                LocalDateTime to,
                                RevenueGroupByEnum groupBy,
                                Long cinemaId,
                                Long roomId,
                                Long movieId);
}
