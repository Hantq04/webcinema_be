package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.response.PrintTicketResponse;

public interface PrintTicketService {
    byte[] generatePdf(String tradingCode);

    PrintTicketResponse getPrintTicketData(String tradingCode);
}