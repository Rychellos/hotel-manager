package pl.rychellos.hotel.authorization.user;

import pl.rychellos.hotel.lib.GenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericRepository<UserEntity> {
    java.util.Optional<UserEntity> findByUsername(String username);
}
