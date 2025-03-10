package vi.wbca.webcinema.service.generalSettingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.GeneralSettingDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.GeneralSettingMapper;
import vi.wbca.webcinema.model.GeneralSetting;
import vi.wbca.webcinema.repository.GeneralSettingRepo;

import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class GeneralSettingServieImpl implements GeneralSettingService{
    private final GeneralSettingRepo generalSettingRepo;
    private final GeneralSettingMapper generalSettingMapper;

    @Override
    public GeneralSettingDTO insertSetting(GeneralSettingDTO generalSettingDTO) {
        GeneralSetting generalSetting = generalSettingMapper.toGeneralSetting(generalSettingDTO);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(generalSettingDTO.getOpenTime());
        calendar.add(Calendar.HOUR_OF_DAY, generalSettingDTO.getBusinessHours());
        generalSetting.setCloseTime(calendar.getTime());

        generalSettingRepo.save(generalSetting);
        return generalSettingMapper.toGeneralSettingDTO(generalSetting);
    }

    @Override
    public void deleteSetting(Long id) {
        GeneralSetting generalSetting = generalSettingRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
        generalSettingRepo.delete(generalSetting);
    }
}
