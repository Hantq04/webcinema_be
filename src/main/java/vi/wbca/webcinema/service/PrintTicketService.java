package vi.wbca.webcinema.service;

public interface PrintTicketService {
    byte[] generatePdf(String tradingCode);
}