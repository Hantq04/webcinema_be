package vi.wbca.webcinema.service.billStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.BillStatus;
import vi.wbca.webcinema.repository.BillStatusRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillStatusServiceImpl implements BillStatusService{
    private final BillStatusRepo billStatusRepo;

    @Override
    public BillStatus insertBillStatus(BillStatus billStatus) {
        return billStatusRepo.save(billStatus);
    }

    @Override
    public List<BillStatus> getAllStatus() {
        return billStatusRepo.findAll();
    }
}
