package pl.rychellos.hotel.authorization.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<PermissionRegistry> permissionRegistries;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public void run(String @NonNull ... args) {
        log.info("Checking if data initialization is needed...");

        // Ensure ROLE_ADMIN exists
        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                log.info("Creating admin role...");

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
            log.info("Creating admin user...");

            //TODO: install script
            String adminUsername = "admin";
            String rawPassword = UUID.randomUUID().toString();

            UserEntity admin = UserEntity.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(rawPassword))
                .email("admin@hotel.com")
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();

            log.warn("Created admin user with information: { \"username\": \"{}\", \"password\": \"{}\"}", adminUsername, rawPassword);

            userRepository.save(admin);

            log.info("Admin user seeded successfully.");
        } else {
            log.info("Seeding not necessary, users present in the database");
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        handlerMapping.getHandlerMethods().forEach((mapping, handlerMethod) -> {
            CheckPermission annotation = handlerMethod.getMethodAnnotation(CheckPermission.class);

            if (annotation != null) {
                savePermissionIfMissing(annotation);
            }
        });
    }

    private void savePermissionIfMissing(CheckPermission annotation) {
        log.info("Creating missing permission {}:{}:{}", annotation.target(), annotation.action().name(), annotation.scope().name());

        String name = String.format("%s_%s_%s",
            annotation.target(),
            annotation.action(),
            annotation.scope());

        if (!permissionRepository.existsByName(name)) {
            PermissionEntity entity = new PermissionEntity();
            entity.setName(name);
            permissionRepository.save(entity);
        }
    }
}
