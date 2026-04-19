package enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class StrategyTypeTest {
	
    @Test
    public void testEnumValues() {
    	StrategyType[] vals = StrategyType.values();
        assertTrue(vals.length >= 4);
        assertEquals(StrategyType.DAY_OF_WEEK, StrategyType.valueOf("DAY_OF_WEEK"));
        assertEquals(StrategyType.TIME_OF_DAY, StrategyType.valueOf("TIME_OF_DAY"));
        assertEquals(StrategyType.SPECIAL_DAYS, StrategyType.valueOf("SPECIAL_DAYS"));
        assertEquals(StrategyType.CAR_TYPE, StrategyType.valueOf("CAR_TYPE"));

    }
    
}
