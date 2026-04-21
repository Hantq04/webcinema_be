package vi.wbca.webcinema.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    String name;

    String imageUrl;

    Boolean isActive;
}