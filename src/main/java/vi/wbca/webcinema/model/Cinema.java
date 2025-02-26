package vi.wbca.webcinema.model;

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
@Table(name = "cinemas")
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "address")
    String address;

    @Column(name = "description")
    String description;

    @Column(name = "code")
    String code;

    @Column(name = "name_of_cinema")
    String nameOfCinema;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "cinema")
    List<Room> rooms;
}
