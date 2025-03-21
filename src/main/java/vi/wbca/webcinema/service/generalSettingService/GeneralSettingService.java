package vi.wbca.webcinema.service.generalSettingService;

import vi.wbca.webcinema.dto.setting.GeneralSettingDTO;

public interface GeneralSettingService {
    GeneralSettingDTO insertSetting(GeneralSettingDTO generalSettingDTO);

    void deleteSetting(Long id);
}
