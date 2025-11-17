package vi.wbca.webcinema.model.movie;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "movie_duration")
    Integer movieDuration;

    @Column(name = "end_date", columnDefinition = "DATETIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date endDate;

    @Column(name = "premiere_date", columnDefinition = "DATETIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date premiereDate;

    @Column(name = "description")
    String description;

    @Column(name = "director")
    String director;

    @Column(name = "image")
    String image;

    @Column(name = "hero_image")
    String heroImage;

    @Column(name = "language")
    String language;

    @Column(name = "name")
    String name;

    @Column(name = "trailer")
    String trailer;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "movie")
    List<Schedule> schedules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_type_id")
    MovieType movieType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_id")
    Rate rate;
}
