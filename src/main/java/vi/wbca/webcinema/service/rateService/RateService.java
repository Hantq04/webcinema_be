package vi.wbca.webcinema.service.rateService;

import vi.wbca.webcinema.model.Rate;

import java.util.List;

public interface RateService {
    Rate insertRate(Rate rate);

    List<Rate> getAllRate();
}
