package pl.rychellos.hotel.fault;

import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

@Repository
public interface FaultRepository extends GenericRepository<FaultEntity> {
}
