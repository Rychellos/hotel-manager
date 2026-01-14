package pl.rychellos.hotel.authorization.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.lib.BaseEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
public class PermissionEntity implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RoleEntity> roles = new HashSet<>();
}
