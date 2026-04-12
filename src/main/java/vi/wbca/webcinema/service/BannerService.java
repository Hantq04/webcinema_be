package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.request.BannerRequest;
import vi.wbca.webcinema.model.response.BannerResponse;

import java.io.IOException;
import java.util.List;

public interface BannerService {
    void insertBanner(BannerRequest request) throws IOException;

    void deleteBanner(Long id);

    List<BannerResponse> getAllBanner();
}
