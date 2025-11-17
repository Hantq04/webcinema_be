package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.model.setting.GeneralSetting;

@Mapper(componentModel = "spring")
public interface GeneralSettingMapper {
    @Mapping(target = "id", ignore = true)
    GeneralSetting toGeneralSetting(GeneralSettingDTO generalSettingDTO);

    GeneralSettingDTO toGeneralSettingDTO(GeneralSetting generalSetting);
}
