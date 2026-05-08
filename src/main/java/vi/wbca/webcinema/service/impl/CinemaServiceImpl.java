package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.cinema.CinemaDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.CinemaMapper;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.service.CinemaService;
import vi.wbca.webcinema.util.generate.GenerateCinemaCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {
    private final CinemaRepo cinemaRepo;
    private final CinemaMapper cinemaMapper;

    @Override
    public CinemaDTO insertCinema(CinemaDTO request) {
        Cinema cinema = cinemaMapper.toCinema(request);
        cinema.setActive(true);
        cinema.setCode(GenerateCinemaCode.generateCinemaCode(cinema.getAddress()));
        cinemaRepo.save(cinema);
        return cinemaMapper.toCinemaDTO(cinema);
    }

    @Override
    public void updateCinema(CinemaDTO request) {
        Cinema cinema = cinemaRepo.findByNameOfCinema(request.getNameOfCinema())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        boolean isAddressChanged = !cinema.getAddress().equals(request.getAddress());
        cinema.setAddress(request.getAddress());
        cinema.setDescription(request.getDescription());
        if (isAddressChanged) {
            cinema.setCode(GenerateCinemaCode.generateCinemaCode(request.getAddress()));
        }
        cinemaRepo.save(cinema);
    }

    @Override
    public void deleteCinema(String code) {
        Cinema cinema = cinemaRepo.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
        cinemaRepo.delete(cinema);
    }

    @Override
    public List<CinemaDTO> getAllCinema() {
        return cinemaRepo.findAllByOrderByIdAsc().stream()
                .map(cinemaMapper::toCinemaDTO)
                .toList();
    }

    @Override
    public List<String> getAllAddressActive() {
        return cinemaRepo.findAllByIsActiveTrueOrderByIdAsc().stream()
                .map(Cinema::getAddress)
                .filter(address -> address != null && !address.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream().toList();
    }

    @Override
    public List<String> getCinemaNamesByAddress(String address) {
        return cinemaRepo.findAllByAddressIgnoreCaseAndIsActiveTrueOrderByNameOfCinemaAsc(address)
                .stream().map(Cinema::getNameOfCinema).toList();
    }
}
