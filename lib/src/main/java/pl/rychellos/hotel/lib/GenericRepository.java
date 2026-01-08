package pl.rychellos.hotel.lib;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, Long> {
    T getReferenceById(Long id);

    Optional<T> findByPublicId(UUID publicId);

    boolean existsByPublicId(UUID publicId);

    Page<T> findAll(Specification<T> specification, Pageable pageable);

    Collection<T> findAll(Specification<T> specification);
}