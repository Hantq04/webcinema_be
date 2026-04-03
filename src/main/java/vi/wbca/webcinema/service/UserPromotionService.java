package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.ticket.UserPromotionDTO;
import vi.wbca.webcinema.model.request.SavePromotionRequest;

import java.util.List;

public interface UserPromotionService {
    UserPromotionDTO savePromotion(SavePromotionRequest request);
    
    List<UserPromotionDTO> getUserPromotions(Long userId);
    
    List<UserPromotionDTO> getUnusedPromotions(Long userId);

    List<UserPromotionDTO> getExpiredPromotions(Long userId);
}
