package pl.rychellos.hotel.lib;


import lombok.Data;

import java.util.List;

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
