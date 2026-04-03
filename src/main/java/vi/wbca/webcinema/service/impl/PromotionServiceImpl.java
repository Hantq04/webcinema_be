package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.ticket.PromotionDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.PromotionMapper;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.user.RankCustomer;
import vi.wbca.webcinema.model.response.PromotionResponse;
import vi.wbca.webcinema.repository.bill.PromotionRepo;
import vi.wbca.webcinema.repository.user.RankCustomerRepo;
import vi.wbca.webcinema.service.PromotionService;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepo promotionRepo;
    private final PromotionMapper promotionMapper;
    private final RankCustomerRepo rankCustomerRepo;

    @Override
    public void insertPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = promotionMapper.toPromotion(promotionDTO);
        promotion.setCode(GenerateCode.generateCode());
        promotion.setPromotionType(promotionDTO.getPromotionType());
        RankCustomer rankCustomer = rankCustomerRepo.findByName(promotionDTO.getNameRankCustomer())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        if (promotionDTO.getStartTime() == null) {
            promotion.setStartTime(LocalDateTime.now());
        } else {
            promotion.setStartTime(promotionDTO.getStartTime());
        }
        promotion.setEndTime(promotion.getStartTime().plusHours(24));
        promotion.setActive(true);
        promotion.setRankCustomer(rankCustomer);
        promotionRepo.save(promotion);
    }

    @Override
    public void deletePromotion(String name) {
        Promotion promotion = promotionRepo.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        promotionRepo.delete(promotion);
    }

    @Override
    public List<PromotionResponse> getAllPromotion() {
        return promotionRepo.findAll().stream()
                .map(promotionMapper::toPromotionResponse)
                .collect(Collectors.toList());
    }
}
