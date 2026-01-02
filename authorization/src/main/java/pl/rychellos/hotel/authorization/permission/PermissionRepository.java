package pl.rychellos.hotel.authorization.permission;

import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends GenericRepository<PermissionEntity> {
    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);
}