package enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class DecoratorTypeTest {
	
    @Test
    public void testEnumValues() {
    	DecoratorType[] vals = DecoratorType.values();
        assertTrue(vals.length >= 4);
        assertEquals(DecoratorType.DAY_OF_WEEK, DecoratorType.valueOf("DAY_OF_WEEK"));
        assertEquals(DecoratorType.TIME_OF_DAY, DecoratorType.valueOf("TIME_OF_DAY"));
        assertEquals(DecoratorType.SPECIAL_DAYS, DecoratorType.valueOf("SPECIAL_DAYS"));
        assertEquals(DecoratorType.CAR_TYPE, DecoratorType.valueOf("CAR_TYPE"));

    }
    
}
