package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.model.user.RankCustomer;
import vi.wbca.webcinema.repository.user.RankCustomerRepo;
import vi.wbca.webcinema.service.RankCustomerService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankCustomerServiceImpl implements RankCustomerService {
    private final RankCustomerRepo rankCustomerRepo;

    @Override
    @Transactional
    public RankCustomer insertRank(RankCustomer rankCustomer) {
        rankCustomer.setActive(true);
        return rankCustomerRepo.save(rankCustomer);
    }

    @Override
    public List<RankCustomer> getAllRank() {
        return rankCustomerRepo.findAll();
    }
}
