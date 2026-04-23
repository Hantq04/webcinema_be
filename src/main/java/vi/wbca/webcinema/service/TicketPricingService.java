package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.movie.Schedule;

public interface TicketPricingService {
    Long calculateFinalPrice(Schedule schedule, String seatTypeName);
}
