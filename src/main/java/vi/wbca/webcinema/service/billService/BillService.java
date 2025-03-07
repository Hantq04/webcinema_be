package vi.wbca.webcinema.service.billService;

import vi.wbca.webcinema.dto.BillDTO;

public interface BillService {
    BillDTO insertBill(BillDTO billDTO);

    void updateBill(BillDTO billDTO);

    void deleteBill(String code);
}
