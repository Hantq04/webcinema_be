package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.GeneralSetting;

@Repository
public interface GeneralSettingRepo extends JpaRepository<GeneralSetting, Long> {
}
