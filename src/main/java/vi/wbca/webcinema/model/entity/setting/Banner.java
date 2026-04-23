package vi.wbca.webcinema.model.entity.setting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.model.entity.movie.Movie;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "banners")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "title")
    String title;

    @Column(name = "title_en")
    String titleEn;

    @JsonIgnore
    @OneToOne(mappedBy = "banner", fetch = FetchType.LAZY)
    Movie movie;
}
