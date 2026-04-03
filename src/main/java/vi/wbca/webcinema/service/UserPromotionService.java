package vi.wbca.webcinema.service;

import vi.wbca.webcinema.enums.PromotionTypeEnum;
import vi.wbca.webcinema.enums.VoucherStatusEnum;
import vi.wbca.webcinema.model.dto.ticket.UserPromotionDTO;
import vi.wbca.webcinema.model.request.SavePromotionRequest;
import vi.wbca.webcinema.model.request.ViewVoucherRequest;

import java.util.List;

public interface UserPromotionService {
    void savePromotion(SavePromotionRequest request);
    
    List<UserPromotionDTO> getUserPromotions(ViewVoucherRequest request);
}
