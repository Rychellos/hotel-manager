package pl.rychellos.hotel.authorization.permission;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

@Repository
public interface PermissionRepository extends GenericRepository<PermissionEntity> {
    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);
}
