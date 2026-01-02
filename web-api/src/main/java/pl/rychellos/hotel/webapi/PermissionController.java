package pl.rychellos.hotel.webapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.permission.dto.PermissionFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController extends GenericController<PermissionEntity, PermissionDTO, PermissionFilterDTO> {
    protected PermissionController(
            GenericService<PermissionEntity, PermissionDTO, PermissionFilterDTO> service,
            ApplicationExceptionFactory applicationExceptionFactory,
            LangUtil langUtil) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @CheckPermission(target = "ROOM", action = ActionType.READ, scope = ActionScope.PAGINATED)
    public Page<PermissionDTO> getAll(
            Pageable pageable,
            PermissionFilterDTO filter) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{id}")
    @CheckPermission(target = "PERMISSION", action = ActionType.READ, scope = ActionScope.ONE)
    public PermissionDTO getById(@PathVariable long id) {
        return super.getOne(id);
    }
}
