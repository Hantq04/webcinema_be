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
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "capatity")
    Integer capacity;

    @Column(name = "type")
    Integer type;

    @Column(name = "description")
    String description;

    @Column(name = "code")
    String code;

    @Column(name = "name")
    String name;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "room")
    List<Schedule> schedules;

    @OneToMany(mappedBy = "room")
    List<Seat> seats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    Cinema cinema;
}
