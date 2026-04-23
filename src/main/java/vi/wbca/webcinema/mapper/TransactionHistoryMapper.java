package vi.wbca.webcinema.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper {
    @Mapping(target = "customerName", source = "user.username")
    @Mapping(target = "billStatus", ignore = true)
    @Mapping(target = "cinemaCode", ignore = true)
    @Mapping(target = "cinemaName", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "roomName", ignore = true)
    @Mapping(target = "movieName", ignore = true)
    @Mapping(target = "showTimeName", ignore = true)
    @Mapping(target = "ticketCount", ignore = true)
    @Mapping(target = "foodCount", ignore = true)
    TransactionHistoryResponse toResponse(Bill bill);

    @AfterMapping
    default void enrichBill(@MappingTarget TransactionHistoryResponse response, Bill bill) {
        BillTicket firstBillTicket = getFirstBillTicket(bill);
        Ticket ticket = firstBillTicket != null ? firstBillTicket.getTicket() : null;
        Schedule schedule = ticket != null ? ticket.getSchedule() : null;
        Room room = schedule != null ? schedule.getRoom() : null;
        Cinema cinema = room != null ? room.getCinema() : null;

        response.setCinemaCode(cinema != null ? cinema.getCode() : null);
        response.setCinemaName(cinema != null ? cinema.getNameOfCinema() : null);
        response.setRoomCode(room != null ? room.getCode() : null);
        response.setRoomName(room != null ? room.getName() : null);
        response.setMovieName(schedule != null && schedule.getMovie() != null ? schedule.getMovie().getName() : null);
        response.setShowTimeName(schedule != null ? schedule.getName() : null);
        response.setBillStatus(bill.getBillStatus() != null ? bill.getBillStatus().getName() : null);
        response.setTicketCount(bill.getBillTickets() != null ? bill.getBillTickets().size() : 0);
        response.setFoodCount(bill.getBillFoods() != null ? bill.getBillFoods().size() : 0);
    }

    default BillTicket getFirstBillTicket(Bill bill) {
        return bill.getBillTickets() != null && !bill.getBillTickets().isEmpty()
                ? bill.getBillTickets().get(0)
                : null;
    }
}