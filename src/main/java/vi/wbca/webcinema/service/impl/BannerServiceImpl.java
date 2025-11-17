package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.setting.Banner;
import vi.wbca.webcinema.repository.setting.BannerRepo;
import vi.wbca.webcinema.service.BannerService;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepo bannerRepo;

    @Override
    public void insertBanner(Banner banner) {
        bannerRepo.save(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        Banner banner = bannerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TITLE_NOT_FOUND));
        bannerRepo.delete(banner);
    }
}
