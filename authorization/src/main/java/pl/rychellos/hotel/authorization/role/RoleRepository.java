package pl.rychellos.hotel.authorization.role;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

@Repository
public interface RoleRepository extends GenericRepository<RoleEntity> {
    Optional<RoleEntity> findByInternalName(String name);
}
