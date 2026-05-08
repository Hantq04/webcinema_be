package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.cinema.CinemaDTO;

import java.util.List;

public interface CinemaService {
    CinemaDTO insertCinema(CinemaDTO cinemaDTO);

    void updateCinema(CinemaDTO cinemaDTO);

    void deleteCinema(String code);

    List<CinemaDTO> getAllCinema();

    List<String> getAllAddressActive();

    List<String> getCinemaNamesByAddress(String address);
}
