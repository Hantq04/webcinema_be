package vi.wbca.webcinema.service.rateService;

import vi.wbca.webcinema.model.movie.Rate;

import java.util.List;

public interface RateService {
    Rate insertRate(Rate rate);

    List<Rate> getAllRate();
}
