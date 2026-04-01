package vi.wbca.webcinema.model.entity.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.TokenStatusEnum;
import vi.wbca.webcinema.model.entity.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "access_tokens")
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "token_status")
    @Enumerated(EnumType.STRING)
    TokenStatusEnum tokenStatus;

    @Column(name = "access_token")
    String accessToken;

    @Column(name = "expires_in")
    Long expiresIn;

    @Column(name = "expired_at", columnDefinition = "DATETIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
