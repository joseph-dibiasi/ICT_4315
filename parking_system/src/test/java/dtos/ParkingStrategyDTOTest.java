package dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import enums.StrategyType;
import factories.StrategyFactoryConfig;

class ParkingStrategyDTOTest {

    @Test
    void accessorsWork() {
        ParkingStrategyDTO dto = new ParkingStrategyDTO();
        assertNull(dto.getStrategyType());

        dto.setStrategyType(StrategyType.CAR_TYPE);
        StrategyFactoryConfig cfg = StrategyFactoryConfig.builder().rateModifier(0.5).build();
        dto.setStrategyConfig(cfg);

        assertEquals(StrategyType.CAR_TYPE, dto.getStrategyType());
        assertEquals(0.5, dto.getStrategyConfig().getRateModifier());
    }

}
