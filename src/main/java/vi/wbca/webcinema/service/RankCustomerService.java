package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.user.RankCustomer;

import java.util.List;

public interface RankCustomerService {
    void insertRank(RankCustomer rankCustomer);

    List<RankCustomer> getAllRank();
}
