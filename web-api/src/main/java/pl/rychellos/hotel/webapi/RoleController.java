package pl.rychellos.hotel.webapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.role.dto.RoleFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController extends GenericController<RoleEntity, RoleDTO, RoleFilterDTO> {

    protected RoleController(GenericService<RoleEntity, RoleDTO, RoleFilterDTO> service,
                             ApplicationExceptionFactory applicationExceptionFactory, LangUtil langUtil) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    public Page<RoleDTO> getAll(Pageable pageable, RoleFilterDTO filter) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{id}")
    public RoleDTO getById(@PathVariable long id) {
        return super.getOne(id);
    }
}
