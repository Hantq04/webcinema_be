package vi.wbca.webcinema.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vi.wbca.webcinema.enums.RoleEnum;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.setting.ConfirmEmail;
import vi.wbca.webcinema.model.entity.token.AccessToken;
//import vi.wbca.webcinema.model.entity.token.RefreshToken;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_name")
    String userName;

    @Column(name = "password")
    String password;

    @Column(name = "is_active")
    boolean isActive;

    @OneToMany(mappedBy = "user")
    List<Bill> bills;

    @OneToMany(mappedBy = "user")
    List<ConfirmEmail> confirmEmails;

    @OneToMany(mappedBy = "user")
    List<AccessToken> accessTokens;

//    @OneToMany(mappedBy = "user")
//    List<RefreshToken> refreshTokens;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserChangeHistory> changeHistories;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    RoleEnum role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_customer_id")
    RankCustomer rankCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_id")
    UserStatus userStatus;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
