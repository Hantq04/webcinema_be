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
@Table(name = "movie_types")
public class MovieType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "movie_type_name")
    String movieTypeNameVi;

    @Column(name = "movie_type_name_en")
    String movieTypeNameEn;

    @Column(name = "is_active")
    boolean isActive;

    @ManyToMany(mappedBy = "movieTypes")
    List<Movie> movies;
}
