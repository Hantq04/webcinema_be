package vi.wbca.webcinema.service;

import vi.wbca.webcinema.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.model.setting.GeneralSetting;

import java.util.List;

public interface GeneralSettingService {
    GeneralSettingDTO insertSetting(GeneralSettingDTO generalSettingDTO);

    void deleteSetting(Long id);

    List<GeneralSetting> getAllSetting();
}
