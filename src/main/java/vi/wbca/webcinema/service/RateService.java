package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.movie.Rate;

import java.util.List;

public interface RateService {
    Rate insertRate(Rate rate);

    List<Rate> getAllRate();
}
