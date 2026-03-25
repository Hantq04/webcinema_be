package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.setting.Banner;
import vi.wbca.webcinema.model.request.BannerRequest;

import java.io.IOException;
import java.util.List;

public interface BannerService {
    void insertBanner(BannerRequest request) throws IOException;

    void deleteBanner(Long id);

    List<Banner> getAllBanner();
}
