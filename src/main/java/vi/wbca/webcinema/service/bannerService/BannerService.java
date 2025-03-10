package vi.wbca.webcinema.service.bannerService;

import vi.wbca.webcinema.model.Banner;

public interface BannerService {
    void insertBanner(Banner banner);

    void deleteBanner(Long id);
}
