package vi.wbca.webcinema.service.cinemaService;

import vi.wbca.webcinema.dto.cinema.CinemaDTO;

public interface CinemaService {
    CinemaDTO insertCinema(CinemaDTO cinemaDTO);
    void updateCinema(CinemaDTO cinemaDTO);
    void deleteCinema(String code);
}
