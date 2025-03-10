package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.GeneralSettingDTO;
import vi.wbca.webcinema.model.GeneralSetting;

@Mapper(componentModel = "spring")
public interface GeneralSettingMapper {
    @Mapping(target = "id", ignore = true)
    GeneralSetting toGeneralSetting(GeneralSettingDTO generalSettingDTO);

    GeneralSettingDTO toGeneralSettingDTO(GeneralSetting generalSetting);
}
