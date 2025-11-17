package vi.wbca.webcinema.repository.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.setting.Banner;

import java.util.Optional;

@Repository
public interface BannerRepo extends JpaRepository<Banner, Long> {
    Optional<Banner> findByTitle(String title);
}
