package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.movie.Rate;
import vi.wbca.webcinema.model.response.RateResponse;

import java.util.List;

public interface RateService {
    Rate insertRate(Rate rate);

    List<RateResponse> getAllRate();
}
