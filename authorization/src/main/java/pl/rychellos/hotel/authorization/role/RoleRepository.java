package pl.rychellos.hotel.authorization.role;

import pl.rychellos.hotel.lib.GenericRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends GenericRepository<RoleEntity> {
    Optional<RoleEntity> findByName(String name);
}
