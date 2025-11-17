package vi.wbca.webcinema.service.rateService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.movie.Rate;
import vi.wbca.webcinema.repository.movie.RateRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService{
    private final RateRepo rateRepo;

    @Override
    public Rate insertRate(Rate rate) {
        return rateRepo.save(rate);
    }

    @Override
    public List<Rate> getAllRate() {
        return rateRepo.findAll();
    }
}
