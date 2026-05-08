package factories;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import enums.CarType;

class DecoratorFactoryConfigTest {

    @Test
    void builderProducesExpectedConfig() {
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);

        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder()
                .carTypes(List.of(CarType.COMPACT))
                .daysOfWeek(List.of(java.time.DayOfWeek.MONDAY))
                .specialDays(List.of(25))
                .timeOfDayRange(new Instant[] { start, end })
                .rateModifier(0.75)
                .build();

        assertNotNull(cfg);
        assertEquals(0.75, cfg.getRateModifier());
        assertEquals(1, cfg.getCarTypes().size());
        assertEquals(1, cfg.getDaysOfWeek().size());
        assertEquals(1, cfg.getSpecialDays().size());
    }

}
