package pl.rychellos.hotel.authorization.role;

import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

import java.util.Optional;

@Repository
public interface RoleRepository extends GenericRepository<RoleEntity> {
    Optional<RoleEntity> findByInternalName(String name);
}
