package pl.rychellos.hotel.authorization.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.rychellos.hotel.authorization.user.UserEntity;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash; // We will store hashed token for security

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private long accessTokenCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
