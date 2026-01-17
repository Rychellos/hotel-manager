package pl.rychellos.hotel.lib;


import java.util.List;
import lombok.Data;

@Data
public class JSONPatchDTO {
    public record Operation(
        String op,
        String path,
        Object value
    ) {
    }

    private List<Operation> body;
}
