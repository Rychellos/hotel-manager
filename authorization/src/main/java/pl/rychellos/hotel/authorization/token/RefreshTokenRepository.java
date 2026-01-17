package pl.rychellos.hotel.authorization.token;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.authorization.user.UserEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findByUserAndRevokedFalseAndExpiryDateAfter(UserEntity user, Instant now);

    List<RefreshTokenEntity> findByUser(UserEntity user);

    void deleteByUserId(Long userId);

    void deleteByExpiryDateBefore(Instant expiryDate);
}
