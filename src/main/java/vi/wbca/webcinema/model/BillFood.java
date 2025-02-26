package vi.wbca.webcinema.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "bill_foods")
public class BillFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "bill_id")
    Integer billId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    Food food;
}
