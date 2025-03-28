package vi.wbca.webcinema.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.PromotionType;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "percent")
    Integer percent;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "promotion_type")
    @Enumerated(EnumType.STRING)
    PromotionType promotionType;

    @Column(name = "start_time", columnDefinition = "DATETIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date startTime;

    @Column(name = "end_time", columnDefinition = "DATETIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date endTime;

    @Column(name = "description")
    String description;

    @Column(name = "name")
    String name;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "promotion")
    List<Bill> bills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_customer_id")
    RankCustomer rankCustomer;
}
