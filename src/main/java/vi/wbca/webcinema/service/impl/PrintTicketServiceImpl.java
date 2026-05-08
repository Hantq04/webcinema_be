package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.response.PrintTicketItemResponse;
import vi.wbca.webcinema.model.response.PrintTicketResponse;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.service.PrintTicketService;
import vi.wbca.webcinema.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrintTicketServiceImpl implements PrintTicketService {
    private final BillRepo billRepo;

    @Override
    public byte[] generatePdf(String tradingCode) {
        PrintTicketResponse responseData = getPrintTicketData(tradingCode);

        try (InputStream inputStream = new ClassPathResource("template/PrintTicketPdf.jrxml").getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            Map<String, Object> parameters = buildParameters(responseData);
            JRDataSource dataSource = new JRBeanCollectionDataSource(
                    responseData.getTickets() == null ? Collections.emptyList() : responseData.getTickets()
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (JRException e) {
            log.error("Failed to generate print ticket PDF for tradingCode={}", tradingCode, e);
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public PrintTicketResponse getPrintTicketData(String code) {
        Bill bill = billRepo.findByTradingCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));

        if (bill.getBillStatus() == null || !BillStatusEnum.SUCCESS.name().equalsIgnoreCase(bill.getBillStatus().getName())) {
            throw new AppException(ErrorCode.PRINT_TICKET_NOT_ALLOWED);
        }

        List<PrintTicketItemResponse> printTickets = new ArrayList<>();
        if (bill.getBillTickets() != null) {
            for (BillTicket billTicket : bill.getBillTickets()) {
                Ticket ticket = billTicket.getTicket();
                if (ticket == null || ticket.getSeat() == null) {
                    continue;
                }
                printTickets.add(PrintTicketItemResponse.builder()
                        .ticketCode(ticket.getCode())
                        .seatCode(ticket.getSeat().getLine() + ticket.getSeat().getNumber())
                        .seatType(ticket.getSeat().getSeatType() != null ? ticket.getSeat().getSeatType().getNameType() : null)
                    .ticketPrice(ticket.getPriceTicket() != null ? BigDecimal.valueOf(ticket.getPriceTicket()) : null)
                    .vatAmount(ticket.getPriceTicket() != null ? calculateVatAmount(BigDecimal.valueOf(ticket.getPriceTicket())) : null)
                    .totalAmount(ticket.getPriceTicket() != null ? BigDecimal.valueOf(ticket.getPriceTicket())
                            .add(calculateVatAmount(BigDecimal.valueOf(ticket.getPriceTicket())))
                            .setScale(0, java.math.RoundingMode.HALF_UP) : null)
                        .build());
            }
        }

        String cinemaName = null;
        String cinemaAddress = null;
        String roomName = null;
        String roomCode = null;
        String roomType = null;
        String movieName = null;
        LocalDateTime startAt = null;
        LocalDateTime endAt = null;
        String showTimeName = null;

        if (bill.getBillTickets() != null && !bill.getBillTickets().isEmpty() && bill.getBillTickets().get(0).getTicket() != null) {
            Ticket ticket = bill.getBillTickets().get(0).getTicket();
            if (ticket.getSchedule() != null) {
                startAt = ticket.getSchedule().getStartAt();
                endAt = ticket.getSchedule().getEndAt();
                showTimeName = ticket.getSchedule().getName();
                movieName = ticket.getSchedule().getMovie() != null ? ticket.getSchedule().getMovie().getName() : null;
                if (ticket.getSchedule().getRoom() != null) {
                    roomName = ticket.getSchedule().getRoom().getName();
                    roomCode = ticket.getSchedule().getRoom().getCode();
                    roomType = ticket.getSchedule().getRoom().getType() != null ? ticket.getSchedule().getRoom().getType().name() : null;
                    if (ticket.getSchedule().getRoom().getCinema() != null) {
                        cinemaName = ticket.getSchedule().getRoom().getCinema().getNameOfCinema();
                        cinemaAddress = ticket.getSchedule().getRoom().getCinema().getAddress();
                    }
                }
            }
        }

        return PrintTicketResponse.builder()
                .tradingCode(bill.getTradingCode())
                .customerName(bill.getUser() != null ? bill.getUser().getUsername() : null)
                .cinemaName(cinemaName)
                .cinemaAddress(cinemaAddress)
                .roomName(roomName)
                .roomCode(roomCode)
                .roomType(roomType)
                .movieName(movieName)
                .startAt(startAt)
                .endAt(endAt)
                .showTimeName(showTimeName)
                .totalMoney(bill.getTotalMoney())
                .billStatus(bill.getBillStatus() != null ? bill.getBillStatus().getName() : null)
                .tickets(printTickets)
                .build();
    }

    private Map<String, Object> buildParameters(PrintTicketResponse responseData) {
        Map<String, Object> parameters = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        parameters.put("TRADING_CODE", responseData.getTradingCode());
        parameters.put("CUSTOMER_NAME", responseData.getCustomerName());
        parameters.put("CINEMA_NAME", responseData.getCinemaName());
        parameters.put("CINEMA_ADDRESS", responseData.getCinemaAddress());
        parameters.put("ROOM_TYPE", responseData.getRoomType());
        parameters.put("MOVIE_NAME", responseData.getMovieName());
        parameters.put("SHOW_TIME_NAME", responseData.getShowTimeName());
        parameters.put("BILL_STATUS", responseData.getBillStatus());
        
        // Pre-compute datetime range
        String startAtText = responseData.getStartAt() != null
                ? responseData.getStartAt().atZone(ZoneId.systemDefault()).format(formatter) : "";
        String endAtText = responseData.getEndAt() != null
                ? responseData.getEndAt().atZone(ZoneId.systemDefault()).format(formatter) : "";
        String datetimeRange = startAtText + " - " + endAtText;
        parameters.put("DATETIME_RANGE", datetimeRange);
        
        // Pre-compute room info
        String roomInfo = (responseData.getRoomName() != null ? responseData.getRoomName() : "");
        if (responseData.getRoomCode() != null && !responseData.getRoomCode().isEmpty()) {
            roomInfo += " (" + responseData.getRoomCode() + ")";
        }
        parameters.put("ROOM_INFO", roomInfo);

        String salesNoText = "Sales No. " + responseData.getTradingCode();
        parameters.put("SALES_NO_TEXT", salesNoText);
        
        return parameters;
    }

    private BigDecimal calculateVatAmount(BigDecimal ticketPrice) {
        return ticketPrice.multiply(Constants.VAT_RATE).setScale(0, java.math.RoundingMode.HALF_UP);
    }
}