package pl.rychellos.hotel.authorization.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.permission.PermissionRepository;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.RoleRepository;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.lib.security.PermissionRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<PermissionRegistry> permissionRegistries;

    @Override
    public void run(String @NonNull ... args) {
        log.info("Checking if data initialization is needed...");

        // Ensure ROLE_ADMIN exists
        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                RoleEntity role = new RoleEntity();
                role.setName("ROLE_ADMIN");
                role.setDescription("Administrator role");
                return roleRepository.save(role);
            });

        // Auto-discover and seed permissions
        Set<PermissionEntity> allPermissions = new HashSet<>();
        for (PermissionRegistry registry : permissionRegistries) {
            registry.getPermissions().forEach(def -> {
                String pName = def.toPermissionString();
                PermissionEntity permission = permissionRepository.findByName(pName)
                    .orElseGet(() -> {
                        PermissionEntity p = new PermissionEntity();
                        p.setName(pName);
                        return permissionRepository.save(p);
                    });
                allPermissions.add(permission);
            });
        }

        // Grant all permissions to ROLE_ADMIN
        adminRole.getPermissions().addAll(allPermissions);
        roleRepository.save(adminRole);

        if (userRepository.count() == 0) {
            log.info("Seeding initial admin user...");
            UserEntity admin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@hotel.com")
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();

            userRepository.save(admin);
            log.info("Admin user seeded successfully.");
        }
    }
}
