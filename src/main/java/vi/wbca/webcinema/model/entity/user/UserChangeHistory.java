package vi.wbca.webcinema.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_change_histories")
public class UserChangeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    ChangeTypeEnum changeType;

    @Column(name = "old_name")
    String oldName;

    @Column(name = "new_name")
    String newName;

    @Column(name = "old_email")
    String oldEmail;

    @Column(name = "new_email")
    String newEmail;

    @Column(name = "old_phone_number")
    String oldPhoneNumber;

    @Column(name = "new_phone_number")
    String newPhoneNumber;

    @Column(name = "password_changed")
    boolean passwordChanged;

    @Column(name = "changed_at", nullable = false)
    LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
