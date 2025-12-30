package pl.rychellos.hotel.authorization.token;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.rychellos.hotel.authorization.token.dto.TokenDTO;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "username", source = "user.username")
    TokenDTO toDTO(RefreshTokenEntity entity);
}
