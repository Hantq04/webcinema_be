package vi.wbca.webcinema.util.logging.logForm;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorLog {
    String errorLevel;
    String description;
    String location;
}
