package pl.rychellos.hotel.lib;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Production-Grade Specification Builder.
 * Supports: Nested paths, Range queries, Join management, and Strategy-based filtering.
 */
@Slf4j
public class EntitySpecificationBuilder<DTO extends BaseDTO> {
    // Cache to avoid aggressive reflection lookups on every request
    private static final Map<Class<?>, List<CachedField>> FIELD_CACHE = new ConcurrentHashMap<>();

    private record CachedField(Field field, SearchFilter annotation) {
    }

    /**
     * Builds a specification based on the DTO.
     * Note: We do not cache the Specification itself as values change,
     * but we cache the structure introspection.
     */
    public Specification<DTO> build(Object filterDto) {
        if (filterDto == null) {
            return Specification.where((Specification<DTO>) null);
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Check if the entity has a 'deleted' field
            try {
                root.get("deleted");
                predicates.add(cb.equal(root.get("deleted"), false));
            } catch (IllegalArgumentException e) {
                // Entity doesn't have a 'deleted' field, skip silently
            }

            List<CachedField> metadata = getCachedMetadata(filterDto.getClass());

            // Map to store joins to prevent duplicate joins on the same table
            Map<String, Join<?, ?>> joinMap = new HashMap<>();

            for (CachedField cachedField : metadata) {
                try {
                    Object value = cachedField.field.get(filterDto);
                    SearchFilter annotation = cachedField.annotation;

                    if (value == null && annotation.ignoreIfNull()) {
                        continue;
                    }

                    // Handle empty strings for LIKE queries
                    if (value instanceof String s && s.trim().isEmpty() && annotation.ignoreIfNull()) {
                        continue;
                    }

                    // Determine the entity path (e.g., "user.role.authority")
                    String propertyPath = StringUtils.hasText(annotation.path())
                        ? annotation.path()
                        : cachedField.field.getName();

                    Path<?> path = getPath(root, propertyPath, joinMap);

                    Predicate predicate = buildPredicate(cb, path, annotation.operator(), value);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }

                } catch (IllegalAccessException e) {
                    log.error("Failed to access field: {}", cachedField.field.getName(), e);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, SearchFilter.Operator operator, Object value) {
        switch (operator) {
            case EQUAL:
                return criteriaBuilder.equal(path, value);
            case NOT_EQUAL:
                return criteriaBuilder.notEqual(path, value);
            case LIKE_IGNORE_CASE:
                if (value instanceof String text) {
                    return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), "%" + text.toLowerCase() + "%");
                }

                log.warn("LIKE operation requires String value.");
                return null;
            case STARTING_WITH:
                if (value instanceof String text) {
                    return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), text.toLowerCase() + "%");
                }

                return null;
            case IN:
                if (value instanceof Collection<?> collection && !collection.isEmpty()) {
                    return path.in(collection);
                }

                return null;
            // Dates, Numbers
            case GREATER_THAN:
                return value instanceof Comparable c ? criteriaBuilder.greaterThan(path.as(Comparable.class), c) : null;
            case GREATER_THAN_EQ:
                return value instanceof Comparable c ? criteriaBuilder.greaterThanOrEqualTo(path.as(Comparable.class), c) : null;
            case LESS_THAN:
                return value instanceof Comparable c ? criteriaBuilder.lessThan(path.as(Comparable.class), c) : null;
            case LESS_THAN_EQ:
                return value instanceof Comparable c ? criteriaBuilder.lessThanOrEqualTo(path.as(Comparable.class), c) : null;
            // Boolean triggers
            case IS_NULL:
                return Boolean.TRUE.equals(value) ? criteriaBuilder.isNull(path) : criteriaBuilder.isNotNull(path);
            case IS_NOT_NULL:
                return Boolean.TRUE.equals(value) ? criteriaBuilder.isNotNull(path) : criteriaBuilder.isNull(path);
            default:
                return null;
        }
    }

    /**
     * Navigates the entity graph. Uses Joins for Collections to avoid Cross Joins.
     */
    private Path<?> getPath(Root<DTO> root, String propertyPath, Map<String, Join<?, ?>> joinMap) {
        if (!propertyPath.contains(".")) {
            return root.get(propertyPath);
        }

        Path<?> path = root;
        String[] parts = propertyPath.split("\\.");
        StringBuilder keyBuilder = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            keyBuilder.append(part);
            String currentKey = keyBuilder.toString();

            // If we are at the last part, just return get()
            if (i == parts.length - 1) {
                path = path.get(part);
            } else {
                // If it's an intermediate node, check if it's a Collection or a Single entity
                // We blindly assume Join needed for complex paths to ensure correct filtering,
                // or we could check the Metamodel. Here we use a simpler reuse strategy.
                // NOTE: For true production safety, check `root.getModel().getAttribute(part).isCollection()`

                Path<?> finalPath = path;

                path = joinMap.computeIfAbsent(currentKey, k -> {
                    if (finalPath instanceof From from) {
                        return from.join(part, JoinType.LEFT);
                    }

                    throw new IllegalStateException("Cannot join on non-From path: " + part);
                });
            }
            keyBuilder.append(".");
        }
        return path;
    }

    private List<CachedField> getCachedMetadata(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, c -> {
            List<CachedField> list = new ArrayList<>();

            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(SearchFilter.class)) {
                    field.setAccessible(true);
                    list.add(new CachedField(field, field.getAnnotation(SearchFilter.class)));
                }
            }

            return list;
        });
    }
}