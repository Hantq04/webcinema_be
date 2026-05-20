package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;

import java.util.List;

public interface GeneralSettingService {
    GeneralSettingDTO insertSetting(GeneralSettingDTO generalSettingDTO);

    GeneralSettingDTO updateSetting(GeneralSettingDTO generalSettingDTO);

    GeneralSettingDTO getLatestSetting();

    void deleteSetting(Long id);

    List<GeneralSetting> getAllSetting();
}
