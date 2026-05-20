package vi.wbca.webcinema.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.entity.user.UserProfile;

import java.util.Optional;

@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);

    Optional<UserProfile> findByUserId(Long userId);

    Optional<UserProfile> findByEmail(String email);

    @Query("""
            select case when count(up) > 0 then true else false end
            from UserProfile up
            join up.user u
            where up.email = :email
                and u.isActive = true
            """)
    boolean existsByEmailAndUserIsActiveTrue(@Param("email") String email);

    @Query("""
            select case when count(up) > 0 then true else false end
            from UserProfile up
            join up.user u
            where up.phoneNumber = :phoneNumber
                and u.isActive = true
            """)
    boolean existsByPhoneNumberAndUserIsActiveTrue(@Param("phoneNumber") String phoneNumber);

    @Query("""
            select case when count(up) > 0 then true else false end
            from UserProfile up
            join up.user u
            where up.email = :email
                and u.id <> :userId
                and u.isActive = true
            """)
    boolean existsByEmailAndUserIdNotAndUserIsActiveTrue(@Param("email") String email,
            @Param("userId") Long userId);

    @Query("""
            select case when count(up) > 0 then true else false end
            from UserProfile up
            join up.user u
            where up.phoneNumber = :phoneNumber
                and u.id <> :userId
                and u.isActive = true
            """)
    boolean existsByPhoneNumberAndUserIdNotAndUserIsActiveTrue(@Param("phoneNumber") String phoneNumber,
            @Param("userId") Long userId);
}
