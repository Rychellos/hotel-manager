package pl.rychellos.hotel.authorization.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RequestMappingHandlerMapping handlerMapping;

    public void checkAdminRole() {
        log.info("Checking if admin role exists...");

        Optional<RoleEntity> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
        RoleEntity adminRole;

        if (adminRoleOptional.isEmpty()) {
            log.info("Admin role not found. Creating ROLE_ADMIN");

            RoleEntity role = new RoleEntity();
            role.setName("ROLE_ADMIN");
            role.setDescription("Administrator role");

            adminRole = role;
        } else {
            log.info("Admin role found. Aborting.");
            adminRole = adminRoleOptional.get();
        }

        List<PermissionEntity> allPermissions = permissionRepository.findAll();

        // Grant all permissions to ROLE_ADMIN
        if (adminRole.getPermissions().isEmpty()) {
            log.info("Added all permissions to ROLE_ADMIN");
            adminRole.getPermissions().addAll(allPermissions);
        }

        if (adminRole.getPermissions().size() != allPermissions.size()) {
            log.info("Added all new permissions to ROLE_ADMIN");
            adminRole.getPermissions().addAll(allPermissions);
        }

        roleRepository.save(adminRole);
    }

    public void checkInitialUser() {
        log.info("Checking if creation of initial user is needed...");

        if (userRepository.count() == 0) {
            log.info("User table is empty. Creating initial user");

            Optional<RoleEntity> roleAdmin = roleRepository.findByName("ROLE_ADMIN");

            if (roleAdmin.isEmpty()) {
                log.warn("Could not find ROLE_ADMIN. Cannot create initial user without admin role. Aborting.");
                return;
            }

            //TODO: install script
            String adminUsername = "admin";
            String rawPassword = UUID.randomUUID().toString();

            UserEntity admin = new UserEntity(
                null,
                adminUsername,
                passwordEncoder.encode(rawPassword),
                null,
                "admin@hotel.com",
                Set.of(roleAdmin.get())
            );

            log.warn("Created admin user with information: { \"username\": \"{}\", \"password\": \"{}\"}", adminUsername, rawPassword);

            userRepository.save(admin);

            log.info("Admin user created successfully.");
        } else {
            log.info("User table is not empty. Aborting.");
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

        this.checkAdminRole();
        this.checkInitialUser();
    }

    private void savePermissionIfMissing(CheckPermission annotation) {
        String name = String.format("%s:%s:%s",
            annotation.target().toUpperCase(),
            annotation.action(),
            annotation.scope()
        );

        if (!permissionRepository.existsByName(name)) {
            log.info("Creating missing permission {}:{}:{}", annotation.target(), annotation.action().name(), annotation.scope().name());

            PermissionEntity entity = new PermissionEntity();
            entity.setName(name);
            permissionRepository.save(entity);
        }
    }
}
