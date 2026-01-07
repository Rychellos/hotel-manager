package pl.rychellos.hotel.currencyexchange;


import org.mapstruct.Mapper;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper extends GenericMapper<CurrencyEntity, CurrencyDTO> {
}