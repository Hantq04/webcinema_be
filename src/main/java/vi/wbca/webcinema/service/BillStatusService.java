package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.bill.BillStatus;

import java.util.List;

public interface BillStatusService {
    BillStatus insertBillStatus(BillStatus billStatus);

    List<BillStatus> getAllStatus();
}
