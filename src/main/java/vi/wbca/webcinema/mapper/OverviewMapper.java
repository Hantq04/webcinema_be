package vi.wbca.webcinema.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.response.PromotionResponse;
import vi.wbca.webcinema.model.response.overview.OverviewRecentBookingResponse;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OverviewMapper {
    @Mapping(target = "customerName", source = "user.username")
    @Mapping(target = "billStatus", source = "billStatus.name")
    @Mapping(target = "movieName", ignore = true)
    @Mapping(target = "showTimeName", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "seatCodes", ignore = true)
    OverviewRecentBookingResponse toRecentBookingResponse(Bill bill);

    @AfterMapping
    default void enrichRecentBooking(@MappingTarget OverviewRecentBookingResponse response, Bill bill) {
        BillTicket firstBillTicket = getFirstBillTicket(bill);
        Ticket ticket = firstBillTicket != null ? firstBillTicket.getTicket() : null;
        Schedule schedule = ticket != null ? ticket.getSchedule() : null;
        Room room = schedule != null ? schedule.getRoom() : null;

        response.setMovieName(schedule != null && schedule.getMovie() != null ? schedule.getMovie().getName() : null);
        response.setShowTimeName(schedule != null ? schedule.getName() : null);
        response.setRoomCode(room != null ? room.getCode() : null);
        response.setSeatCodes(extractSeatCodes(bill));
    }

    @Mapping(target = "rankCustomerName", source = "rankCustomer.name")
    PromotionResponse toPromotionResponse(Promotion promotion);

    default BillTicket getFirstBillTicket(Bill bill) {
        return bill.getBillTickets() != null && !bill.getBillTickets().isEmpty()
                ? bill.getBillTickets().get(0) : null;
    }

    default List<String> extractSeatCodes(Bill bill) {
        return bill.getBillTickets() == null ? List.of() : bill.getBillTickets().stream()
                .map(BillTicket::getTicket)
                .filter(ticket -> ticket != null && ticket.getSeat() != null)
                .map(ticket -> ticket.getSeat().getLine() + ticket.getSeat().getNumber())
                .toList();
    }
}