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
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "code")
    String code;

    @Column(name = "price_ticket")
    Double priceTicket;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "ticket")
    List<BillTicket> billTickets;
}
