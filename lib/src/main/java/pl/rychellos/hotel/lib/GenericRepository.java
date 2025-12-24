package pl.rychellos.hotel.lib;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, Long> {
    T getById(Long id);

    Page<T> findAll(Specification<T> specification, Pageable pageable);

    Collection<T> findAll(Specification<T> specification);
}