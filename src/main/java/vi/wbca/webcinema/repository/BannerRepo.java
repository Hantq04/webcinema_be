package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Banner;

@Repository
public interface BannerRepo extends JpaRepository<Banner, Long> {
}
