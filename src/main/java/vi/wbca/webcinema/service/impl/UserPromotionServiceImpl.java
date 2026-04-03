package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.UserPromotionMapper;
import vi.wbca.webcinema.model.dto.ticket.UserPromotionDTO;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.bill.UserPromotion;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.request.SavePromotionRequest;
import vi.wbca.webcinema.repository.bill.PromotionRepo;
import vi.wbca.webcinema.repository.bill.UserPromotionRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.service.UserPromotionService;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPromotionServiceImpl implements UserPromotionService {
    private final UserPromotionRepo userPromotionRepo;
    private final UserRepo userRepo;
    private final PromotionRepo promotionRepo;
    private final UserPromotionMapper userPromotionMapper;

    @Override
    public UserPromotionDTO savePromotion(SavePromotionRequest request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        Promotion promotion = promotionRepo.findByCode(request.getPromotionCode())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        if (userPromotionRepo.findByUserIdAndPromotionId(request.getUserId(), promotion.getId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATE_PROMOTION);
        }

        UserPromotion userPromotion = new UserPromotion();
        userPromotion.setUser(user);
        userPromotion.setPromotion(promotion);
        userPromotion.setUsed(false);
        
        UserPromotion savedUserPromotion = userPromotionRepo.save(userPromotion);
        
        return userPromotionMapper.toUserPromotionDTO(savedUserPromotion);
    }

    @Override
    public List<UserPromotionDTO> getUserPromotions(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        List<UserPromotion> userPromotions = userPromotionRepo.findByUserId(userId);
        return userPromotions.stream()
                .map(userPromotionMapper::toUserPromotionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserPromotionDTO> getUnusedPromotions(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        List<UserPromotion> userPromotions = userPromotionRepo.findByUserIdAndIsUsedFalse(userId);
        return userPromotions.stream()
                .map(userPromotionMapper::toUserPromotionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserPromotionDTO> getExpiredPromotions(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        List<UserPromotion> userPromotions = userPromotionRepo.findByUserIdAndPromotionEndTimeBefore(userId, LocalDateTime.now());
        return userPromotions.stream()
                .map(userPromotionMapper::toUserPromotionDTO)
                .collect(Collectors.toList());
    }
}
