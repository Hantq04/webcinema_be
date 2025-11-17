package vi.wbca.webcinema.repository.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.setting.GeneralSetting;

import java.util.Optional;

@Repository
public interface GeneralSettingRepo extends JpaRepository<GeneralSetting, Long> {
    Optional<GeneralSetting> findTopByOrderByIdDesc();
}
