package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeneralSettingMapper {
    @Mapping(target = "id", ignore = true)
    GeneralSetting toGeneralSetting(GeneralSettingDTO generalSettingDTO);

    GeneralSettingDTO toGeneralSettingDTO(GeneralSetting generalSetting);
}
