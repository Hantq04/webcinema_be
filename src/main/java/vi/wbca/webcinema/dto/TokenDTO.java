package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenDTO {
    String accessToken;

    String refreshToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    boolean isNewToken;
}
