package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.setting.Banner;

import java.util.List;

public interface BannerService {
    void insertBanner(Banner banner);

    void deleteBanner(Long id);

    List<Banner> getAllBanner();
}
