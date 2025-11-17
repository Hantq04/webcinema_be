package vi.wbca.webcinema.model.cinema;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.model.bill.BillFood;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "foods")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "price")
    Double price;

    @Column(name = "description")
    String description;

    @Column(name = "image")
    String image;

    @Column(name = "name_of_food")
    String nameOfFood;

    boolean isActive;

    @OneToMany(mappedBy = "food")
    List<BillFood> billFoods;
}
