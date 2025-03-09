package vi.wbca.webcinema.service.billStatusService;

import vi.wbca.webcinema.model.BillStatus;

import java.util.List;

public interface BillStatusService {
    BillStatus insertBillStatus(BillStatus billStatus);

    List<BillStatus> getAllStatus();
}
