package enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DayOfWeekTest {

    @Test
    void valuesContainAllDays() {
        DayOfWeek[] vals = DayOfWeek.values();
        assertEquals(7, vals.length);
    }

    @Test
    void valueOfUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> DayOfWeek.valueOf("FUNDAY"));
    }
}
