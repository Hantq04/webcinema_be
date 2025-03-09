package vi.wbca.webcinema.service.billService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.enums.EBillStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.BillMapper;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillStatus;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.BillRepo;
import vi.wbca.webcinema.repository.BillStatusRepo;
import vi.wbca.webcinema.repository.UserRepo;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService{
    private final BillRepo billRepo;
    private final BillMapper billMapper;
    private final UserRepo userRepo;
    private final BillStatusRepo billStatusRepo;

    @Override
    public BillDTO insertBill(BillDTO billDTO) {
        User user = setCustomer(billDTO);
        Bill bill = billMapper.toBill(billDTO);

        bill.setCreateTime(new Date());
        bill.setTradingCode(GenerateCode.generateTradingCode());
        bill.setName("Bill - " + user.getName());
        bill.setUpdateTime(new Date());
        bill.setActive(true);
        bill.setBillStatus(setStatus(EBillStatus.PENDING.toString()));
        bill.setUser(user);

        billRepo.save(bill);
        return billMapper.toBillDTO(bill);
    }

    public User setCustomer(BillDTO billDTO) {
        return userRepo.findByUserName(billDTO.getCustomerName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    public BillStatus setStatus(String eBillStatus) {
        return billStatusRepo.findByName(eBillStatus)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    @Override
    public void deleteBill(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        billRepo.delete(bill);
    }
}
