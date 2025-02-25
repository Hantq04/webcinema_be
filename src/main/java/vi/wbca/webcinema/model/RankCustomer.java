package vi.wbca.webcinema.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rank_customers")
public class RankCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "point")
    Integer point;

    @Column(name = "description")
    String description;

    @Column(name = "name")
    String name;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "rankCustomer")
    @JsonManagedReference
    List<User> users;
}
