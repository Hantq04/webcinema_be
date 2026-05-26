package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.response.TransactionHistoryDetailItemResponse;
import vi.wbca.webcinema.model.response.TransactionHistoryDetailResponse;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper {
        DateTimeFormatter SHOWTIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    @Mapping(target = "customerName", source = "user.username")
    @Mapping(target = "billStatus", source = "billStatus.name")
    TransactionHistoryResponse toResponse(Bill bill);

    @Mapping(target = "billStatus", source = "billStatus.name")
    @Mapping(target = "customerName", source = "user.username")
    @Mapping(target = "items", source = "billTickets")
    TransactionHistoryDetailResponse toDetailResponse(Bill bill);

    default List<TransactionHistoryDetailItemResponse> mapItems(List<BillTicket> billTickets) {
        return billTickets == null ? List.of() : billTickets.stream()
                .map(billTicket -> {
                    Ticket ticket = billTicket.getTicket();
                    Schedule schedule = ticket != null ? ticket.getSchedule() : null;
                    Room room = schedule != null ? schedule.getRoom() : null;
                    return TransactionHistoryDetailItemResponse.builder()
                            .cinemaName(room != null && room.getCinema() != null ? room.getCinema().getNameOfCinema() : null)
                            .roomCode(room != null ? room.getCode() : null)
                            .showTime(formatShowTime(schedule))
                            .seatCode(ticket != null && ticket.getSeat() != null ? ticket.getSeat().getLine() + ticket.getSeat().getNumber() : null)
                            .unitPrice(ticket != null && ticket.getPriceTicket() != null ? BigDecimal.valueOf(ticket.getPriceTicket()) : null)
                            .build();
                }).toList();
    }

    default String formatShowTime(Schedule schedule) {
        if (schedule == null || schedule.getStartAt() == null || schedule.getEndAt() == null) {
            return null;
        }
        return schedule.getStartAt().format(SHOWTIME_FORMATTER) + " - " + schedule.getEndAt().format(SHOWTIME_FORMATTER);
    }
}