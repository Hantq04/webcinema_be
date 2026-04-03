package vi.wbca.webcinema.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import vi.wbca.webcinema.exception.ErrorCode;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseObject {
    int status;
    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object data;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<FieldValidationError> errors;

    public ResponseObject(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseObject(HttpStatus httpStatus, String message, Object data) {
        this.status = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public ResponseObject(ErrorCode errorCode) {
        this.status = errorCode.getStatusCode().value();
        this.message = errorCode.getMessage();
        this.data = null;
    }

    public ResponseObject(int status, String message, List<FieldValidationError> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
