package vi.wbca.webcinema.service.rankCustomerService;

import vi.wbca.webcinema.model.RankCustomer;

import java.util.List;

public interface RankCustomerService {
    RankCustomer insertRank(RankCustomer rankCustomer);

    List<RankCustomer> getAllRank();
}
