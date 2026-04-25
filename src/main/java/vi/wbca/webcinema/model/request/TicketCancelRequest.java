package vi.wbca.webcinema.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCancelRequest {
    @NotEmpty(message = "NOT_BLANK")
    private List<String> ticketCodes;

    private String tradingCode;
}