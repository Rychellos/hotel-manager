package pl.rychellos.hotel.webapi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.room.RoomService;
import pl.rychellos.hotel.room.StandardService;
import pl.rychellos.hotel.room.dto.RoomDTO;
import pl.rychellos.hotel.room.dto.StandardDTO;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {
    private final StandardService standardService;
    private final RoomService roomService;

    public DemoController(StandardService standardService, RoomService roomService) {
        this.standardService = standardService;
        this.roomService = roomService;
    }

    @PostMapping("/populateStandards")
    public void populateStandards() throws ApplicationException {
        standardService
                .saveIfNotExists(new StandardDTO(null, UUID.randomUUID(), "Standard Single", new BigDecimal("150.00"),
                        new BigDecimal("0.00"), "Przytulny pokój jednoosobowy z biurkiem i szybkim Wi-Fi"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Double Classic", new BigDecimal("250.00"),
                new BigDecimal("50.00"), "Klasyczny pokój dla dwóch osób z dużym łóżkiem małżeńskim"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Twin Superior", new BigDecimal("280.00"),
                new BigDecimal("60.00"), "Podwyższony standard z dwoma osobnymi łóżkami i widokiem na miasto"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "King Deluxe", new BigDecimal("450.00"),
                new BigDecimal("100.00"), "Luksusowy pokój z łóżkiem typu King Size i zestawem powitalnym"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Studio z Aneksem", new BigDecimal("320.00"),
                new BigDecimal("40.00"), "Przestronne studio wyposażone w aneks kuchenny i część jadalną"));
        standardService
                .save(new StandardDTO(null, UUID.randomUUID(), "Apartament Prezydencki", new BigDecimal("1200.00"),
                        new BigDecimal("300.00"), "Najwyższy standard, dwie sypialnie, salon i prywatny taras"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Pokój Rodzinny 2+2", new BigDecimal("400.00"),
                new BigDecimal("75.00"), "Duży pokój z dwiema dostawkami, idealny dla rodziców z dziećmi"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Economy Budget", new BigDecimal("120.00"),
                new BigDecimal("30.00"), "Opcja ekonomiczna z podstawowym wyposażeniem w dobrej cenie"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Business Suite", new BigDecimal("550.00"),
                new BigDecimal("120.00"), "Apartament z oddzielną strefą do pracy i ekspresem do kawy"));
        standardService.save(new StandardDTO(null, UUID.randomUUID(), "Pokój typu Loft", new BigDecimal("380.00"),
                new BigDecimal("90.00"), "Nowoczesne wnętrze w stylu industrialnym z wysokim sufitem"));
    }

    @PostMapping("/populateRooms")
    public void createRandomRooms() throws ApplicationException {
        // Pobranie wszystkich dostępnych standardów
        List<StandardDTO> standards = new ArrayList<>(standardService.getAll(null));

        if (standards.isEmpty()) {
            throw new RuntimeException("Brak standardów w bazie. Najpierw uruchom metodę saveHotelStandards().");
        }

        Random random = new Random();

        // Definicja przykładowych pokoi hotelowych
        roomService.save(new RoomDTO(null, null, "Pokój 101", standards.get(random.nextInt(standards.size())).getId(),
                2, "Przytulny pokój na parterze", null, null));
        roomService.save(new RoomDTO(null, null, "Pokój 102", standards.get(random.nextInt(standards.size())).getId(),
                2, "Widok na wewnętrzny dziedziniec", new BigDecimal("180.00"), null));
        roomService
                .save(new RoomDTO(null, null, "Apartament 201", standards.get(random.nextInt(standards.size())).getId(),
                        4, "Luksusowy apartament z balkonem", null, new BigDecimal("120.00")));
        roomService.save(new RoomDTO(null, null, "Pokój 202", standards.get(random.nextInt(standards.size())).getId(),
                1, "Cichy pokój dla singla", null, null));
        roomService.save(new RoomDTO(null, null, "Pokój 301", standards.get(random.nextInt(standards.size())).getId(),
                3, "Pokój rodzinny blisko windy", new BigDecimal("350.00"), new BigDecimal("60.00")));
        roomService.save(new RoomDTO(null, null, "Pokój 302", standards.get(random.nextInt(standards.size())).getId(),
                2, "Standardowy pokój z dwoma łóżkami", null, null));
        roomService.save(new RoomDTO(null, null, "Pokój 401", standards.get(random.nextInt(standards.size())).getId(),
                2, "Pokój na poddaszu z oknem dachowym", new BigDecimal("140.00"), null));
        roomService.save(new RoomDTO(null, null, "Studio 001", standards.get(random.nextInt(standards.size())).getId(),
                2, "Studio z dostępem dla niepełnosprawnych", null, null));
        roomService.save(new RoomDTO(null, null, "Pokój 105", standards.get(random.nextInt(standards.size())).getId(),
                2, "Klasyczny pokój z minibarem", null, null));
        roomService
                .save(new RoomDTO(null, null, "Penthouse 501", standards.get(random.nextInt(standards.size())).getId(),
                        6, "Największy pokój w całym obiekcie", new BigDecimal("2000.00"), new BigDecimal("500.00")));
    }
}
