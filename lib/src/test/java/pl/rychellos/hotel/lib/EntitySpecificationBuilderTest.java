package pl.rychellos.hotel.lib;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.criteria.*;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class EntitySpecificationBuilderTest {

    private EntitySpecificationBuilder<TestEntity> builder;

    @Mock
    private Root<TestEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Path<Object> path;
    @Mock
    private Join<Object, Object> join;

    private static class TestEntity implements BaseEntity {
        @Override
        public Long getId() {
            return 1L;
        }

        @Override
        public java.util.UUID getPublicId() {
            return UUID.randomUUID();
        }

        @Override
        public void setPublicId(UUID publicId) {
            // No-op for testing
        }
    }

    private static class TestFilter {
        @SearchFilter(operator = SearchFilter.Operator.EQUAL)
        private String name;

        @SearchFilter(operator = SearchFilter.Operator.LIKE_IGNORE_CASE)
        private String description;

        @SearchFilter(path = "category.id", operator = SearchFilter.Operator.EQUAL)
        private Long categoryId;

        @SearchFilter(operator = SearchFilter.Operator.IN)
        private Set<Long> ids;

        @SearchFilter(operator = SearchFilter.Operator.IS_NULL)
        private Boolean checkNull;
    }

    @BeforeEach
    void setUp() {
        builder = new EntitySpecificationBuilder<>();
    }

    @Test
    void build_ShouldHandleBasicEquality() {
        /// Given
        TestFilter filter = new TestFilter();
        filter.name = "John";

        when(root.get("name")).thenReturn(path);
        when(root.get("deleted")).thenThrow(new IllegalArgumentException());

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(cb).equal(path, "John");
    }

    @Test
    void build_ShouldHandleLikeIgnoreCase() {
        /// Given
        TestFilter filter = new TestFilter();
        filter.description = "TEST";

        Expression<String> lowerPath = mock(Expression.class);
        when(root.get("description")).thenReturn(path);
        when(cb.lower(any())).thenReturn(lowerPath);
        when(root.get("deleted")).thenThrow(new IllegalArgumentException());

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(cb).like(eq(lowerPath), eq("%test%"));
    }

    @Test
    void build_ShouldHandleJoins_WhenPathContainsDot() {
        /// Given
        TestFilter filter = new TestFilter();
        filter.categoryId = 5L;

        // Path is "category.id"
        when(root.join(eq("category"), any(JoinType.class))).thenReturn(join);
        when(join.get("id")).thenReturn(path);
        when(root.get("deleted")).thenThrow(new IllegalArgumentException());

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(root).join("category", JoinType.LEFT);
        verify(cb).equal(path, 5L);
    }

    @Test
    void build_ShouldHandleInOperator() {
        /// Given
        TestFilter filter = new TestFilter();
        filter.ids = Set.of(1L, 2L);

        CriteriaBuilder.In<Object> inClause = mock(CriteriaBuilder.In.class);
        when(root.get("ids")).thenReturn(path);
        when(path.in(anyCollection())).thenReturn(inClause);
        when(root.get("deleted")).thenThrow(new IllegalArgumentException());

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(path).in(filter.ids);
    }

    @Test
    void build_ShouldIgnoreNullFields_ByDefaultForAnnotation() {
        /// Given
        TestFilter filter = new TestFilter();
        filter.name = null;

        when(root.get("deleted")).thenThrow(new IllegalArgumentException());

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(cb, never()).equal(any(), any());
    }

    @Test
    void build_ShouldAutoApplyDeletedPredicate_IfFieldExists() {
        /// Given
        TestFilter filter = new TestFilter();

        Path<Object> deletedPath = mock(Path.class);

        when(root.get("deleted")).thenReturn(deletedPath);

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(cb).equal(deletedPath, false);
    }

    @Test
    void build_ShouldHandleIsNull_WhenBooleanIsTrue() {
        /// Given
        TestFilter filter = new TestFilter();
        filter.checkNull = true;

        when(root.get("checkNull")).thenReturn(path);
        when(root.get("deleted")).thenThrow(new IllegalArgumentException());

        /// When
        Specification<TestEntity> spec = builder.build(filter);
        spec.toPredicate(root, query, cb);

        /// Then
        verify(cb).isNull(path);
    }
}
