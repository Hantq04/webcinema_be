package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.setting.GeneralSettingDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.GeneralSettingMapper;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.service.GeneralSettingService;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralSettingServieImpl implements GeneralSettingService {
    private final GeneralSettingRepo generalSettingRepo;
    private final GeneralSettingMapper generalSettingMapper;

    @Override
    public GeneralSettingDTO insertSetting(GeneralSettingDTO generalSettingDTO) {
        validateSetting(generalSettingDTO);
        GeneralSetting generalSetting = generalSettingMapper.toGeneralSetting(generalSettingDTO);
        applyComputedFields(generalSetting, generalSettingDTO);
        GeneralSetting savedSetting = generalSettingRepo.save(generalSetting);
        return generalSettingMapper.toGeneralSettingDTO(savedSetting);
    }

    @Override
    public GeneralSettingDTO updateSetting(GeneralSettingDTO generalSettingDTO) {
        if (generalSettingDTO.getId() == null) {
            throw new AppException(ErrorCode.VALIDATE_ERROR);
        }
        validateSetting(generalSettingDTO);

        GeneralSetting generalSetting = generalSettingRepo.findById(generalSettingDTO.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
        applySettingValues(generalSetting, generalSettingDTO);
        GeneralSetting savedSetting = generalSettingRepo.save(generalSetting);
        return generalSettingMapper.toGeneralSettingDTO(savedSetting);
    }

    @Override
    public GeneralSettingDTO getLatestSetting() {
        GeneralSetting generalSetting = generalSettingRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
        return generalSettingMapper.toGeneralSettingDTO(generalSetting);
    }

    @Override
    public void deleteSetting(Long id) {
        GeneralSetting generalSetting = generalSettingRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
        generalSettingRepo.delete(generalSetting);
    }

    @Override
    public List<GeneralSetting> getAllSetting() {
        return generalSettingRepo.findAll();
    }

    private void applySettingValues(GeneralSetting generalSetting, GeneralSettingDTO generalSettingDTO) {
        applyComputedFields(generalSetting, generalSettingDTO);
    }

    private void applyComputedFields(GeneralSetting generalSetting, GeneralSettingDTO generalSettingDTO) {
        generalSetting.setBreakTime(generalSettingDTO.getBreakTime());
        generalSetting.setBusinessHours(generalSettingDTO.getBusinessHours());
        generalSetting.setOpenTime(generalSettingDTO.getOpenTime());
        generalSetting.setCloseTime(generalSettingDTO.getOpenTime().plusHours(generalSettingDTO.getBusinessHours()));
        generalSetting.setPercentWeekend(generalSettingDTO.getPercentWeekend());
        generalSetting.setTimeBeginToChange(generalSettingDTO.getTimeBeginToChange());
        validateBusinessHoursWindow(generalSetting);
    }

    private void validateSetting(GeneralSettingDTO generalSettingDTO) {
        if (generalSettingDTO.getBreakTime() == null
                || generalSettingDTO.getOpenTime() == null
                || generalSettingDTO.getBusinessHours() == null
                || generalSettingDTO.getPercentWeekend() == null
                || generalSettingDTO.getTimeBeginToChange() == null) {
            throw new AppException(ErrorCode.VALIDATE_ERROR);
        }
        if (generalSettingDTO.getBusinessHours() <= 0) {
            throw new AppException(ErrorCode.VALIDATE_ERROR);
        }
        if (generalSettingDTO.getPercentWeekend() < 0 || generalSettingDTO.getPercentWeekend() > 100) {
            throw new AppException(ErrorCode.VALIDATE_ERROR);
        }
        if (generalSettingDTO.getBreakTime().equals(generalSettingDTO.getOpenTime())) {
            throw new AppException(ErrorCode.SHOW_TIME_IN_BREAK);
        }
    }

    private void validateBusinessHoursWindow(GeneralSetting generalSetting) {
        LocalTime openTime = generalSetting.getOpenTime();
        LocalTime closeTime = generalSetting.getCloseTime();
        LocalTime breakTime = generalSetting.getBreakTime();

        if (!isTimeWithinWindow(breakTime, openTime, closeTime)) {
            throw new AppException(ErrorCode.VALIDATE_ERROR);
        }
    }

    private boolean isTimeWithinWindow(LocalTime time, LocalTime openTime, LocalTime closeTime) {
        if (time == null || openTime == null || closeTime == null) {
            return false;
        }
        if (closeTime.equals(openTime)) {
            return false;
        }
        if (closeTime.isAfter(openTime)) {
            return !time.isBefore(openTime) && time.isBefore(closeTime);
        }
        return !time.isBefore(openTime) || time.isBefore(closeTime);
    }
}
