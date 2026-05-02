package enums;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class EventTypeTest {

    @Test
    public void testValues() {
        EventType[] values = EventType.values();
        assertNotNull(values);
        assertTrue(values.length >= 2);
        boolean hasEntry = false;
        boolean hasExit = false;
        for (EventType et : values) {
            if (et.name().equals("ENTRY")) hasEntry = true;
            if (et.name().equals("EXIT")) hasExit = true;
        }
        assertTrue(hasEntry);
        assertTrue(hasExit);
    }
}
