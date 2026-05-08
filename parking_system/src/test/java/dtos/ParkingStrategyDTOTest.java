package dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import enums.DecoratorType;
import factories.DecoratorFactoryConfig;

class ParkingStrategyDTOTest {

    @Test
    void accessorsWork() {
        ParkingStrategyDTO dto = new ParkingStrategyDTO();
        assertNull(dto.getDecoratorType());

        dto.setDecoratorType(DecoratorType.CAR_TYPE);
        DecoratorFactoryConfig cfg = DecoratorFactoryConfig.builder().rateModifier(0.5).build();
        dto.setDecoratorConfig(cfg);

        assertEquals(DecoratorType.CAR_TYPE, dto.getDecoratorType());
        assertEquals(0.5, dto.getDecoratorConfig().getRateModifier());
    }

}
