package models;

import org.junit.jupiter.api.Test;

import models.Money;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testDefaultConstructor() {
        Money money = new Money();
        assertNull(money.getCents());
    }

    @Test
    void testConstructorWithCents() {
        Money money = new Money(150L);
        assertEquals(150L, money.getCents());
    }

    @Test
    void testConstructorWithDollars() {
        Money money = new Money(12.34);
        assertEquals(1234L, money.getCents());
    }

    @Test
    void testConstructorWithDollarsRounding() {
        Money money = new Money(12.999);
        assertEquals(1300L, money.getCents()); // Should round up
    }

    @Test
    void testSetAndGetCents() {
        Money money = new Money();
        money.setCents(999L);
        assertEquals(999L, money.getCents());
    }

    @Test
    void testGetDollarsFromCents() {
        Money money = new Money(250L); // 250 cents
        assertEquals(2.50, money.getDollars(), 0.0001);
    }

    @Test
    void testGetDollarsWhenCentsIsNull() {
        Money money = new Money();
        assertThrows(NullPointerException.class, money::getDollars);
    }

    @Test
    void testToString() {
        Money money = new Money(500L);
        assertEquals("Money [cents=500]", money.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Money m1 = new Money(100L);
        Money m2 = new Money(100L);
        Money m3 = new Money(200L);

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m3);
        assertNotEquals(m1, null);
        assertNotEquals(m1, "not money");
    }
}
