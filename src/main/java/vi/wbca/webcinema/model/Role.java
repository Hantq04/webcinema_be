package vi.wbca.webcinema.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "code")
    String code;

    @Column(name = "role_name")
    String roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    @JsonManagedReference
    List<User> users;
}
