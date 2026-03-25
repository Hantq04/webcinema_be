package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.setting.Banner;
import vi.wbca.webcinema.model.request.BannerRequest;
import vi.wbca.webcinema.repository.setting.BannerRepo;
import vi.wbca.webcinema.service.BannerService;
import vi.wbca.webcinema.util.ImageUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepo bannerRepo;

    @Override
    public void insertBanner(BannerRequest request) throws IOException {
        String imageUrl = ImageUtils.saveImage(request.getFile());

        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setImageUrl(imageUrl);
        bannerRepo.save(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        Banner banner = bannerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TITLE_NOT_FOUND));
        bannerRepo.delete(banner);
    }

    @Override
    public List<Banner> getAllBanner() {
        return bannerRepo.findAll();
    }
}
