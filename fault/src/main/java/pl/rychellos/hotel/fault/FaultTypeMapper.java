package pl.rychellos.hotel.fault;

import org.mapstruct.Mapper;
import pl.rychellos.hotel.fault.dto.FaultTypeDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface FaultTypeMapper extends GenericMapper<FaultTypeEntity, FaultTypeDTO> {
}
