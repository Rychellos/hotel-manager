package pl.rychellos.hotel.lib.lang;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocaleHolder {
    private Locale currentLocale;
}
