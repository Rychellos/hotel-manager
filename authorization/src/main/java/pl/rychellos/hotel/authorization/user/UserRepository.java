package pl.rychellos.hotel.authorization.user;

import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

@Repository
public interface UserRepository extends GenericRepository<UserEntity> {
    java.util.Optional<UserEntity> findByUsername(String username);
}
