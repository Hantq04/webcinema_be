package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.setting.Banner;

public interface BannerService {
    void insertBanner(Banner banner);

    void deleteBanner(Long id);
}
