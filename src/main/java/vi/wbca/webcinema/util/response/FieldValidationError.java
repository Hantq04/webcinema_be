package vi.wbca.webcinema.util.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldValidationError {
    private String field;
    private String message;
}
