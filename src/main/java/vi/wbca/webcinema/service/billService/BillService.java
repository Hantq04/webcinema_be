package vi.wbca.webcinema.service.billService;

import vi.wbca.webcinema.dto.bill.BillDTO;

public interface BillService {
    void createBill(BillDTO billDTO);

    void updateBill(BillDTO billDTO);

    void deleteBill(String code);
}
