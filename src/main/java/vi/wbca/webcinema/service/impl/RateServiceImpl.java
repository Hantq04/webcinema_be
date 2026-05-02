package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.entity.movie.Rate;
import vi.wbca.webcinema.mapper.RateMapper;
import vi.wbca.webcinema.repository.movie.RateRepo;
import vi.wbca.webcinema.service.RateService;
import vi.wbca.webcinema.model.response.RateResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateRepo rateRepo;
    private final RateMapper rateMapper;

    @Override
    public Rate insertRate(Rate rate) {
        return rateRepo.save(rate);
    }

    @Override
    public List<RateResponse> getAllRate() {
        return rateRepo.findAll().stream()
                .map(rateMapper::toRateResponse)
                .toList();
    }
}
