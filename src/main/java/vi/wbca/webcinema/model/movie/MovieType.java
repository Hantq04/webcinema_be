package vi.wbca.webcinema.model.movie;

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
@Table(name = "movie_types")
public class MovieType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "movie_type_name")
    String movieTypeName;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "movieType")
    List<Movie> movies;
}
