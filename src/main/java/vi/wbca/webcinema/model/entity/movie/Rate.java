package vi.wbca.webcinema.model.entity.movie;

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
@Table(name = "rates")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "description")
    String descriptionVi;

    @Column(name = "description_en")
    String descriptionEn;

    @Column(name = "code")
    String code;

    @OneToMany(mappedBy = "rate")
    List<Movie> movies;
}
