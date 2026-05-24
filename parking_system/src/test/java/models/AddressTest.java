package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.builder().build();
    }

    @Test
    void testStreetAddress1GetterAndSetter() {
        address.setStreetAddress1("123 Main St");
        assertEquals("123 Main St", address.getStreetAddress1());
    }

    @Test
    void testStreetAddress2GetterAndSetter() {
        address.setStreetAddress2("Apt 4B");
        assertEquals(" Apt 4B, ", address.getStreetAddress2());
    }

    @Test
    void testCityGetterAndSetter() {
        address.setCity("Springfield");
        assertEquals("Springfield", address.getCity());
    }

    @Test
    void testStateGetterAndSetter() {
        address.setState("IL");
        assertEquals("IL", address.getState());
    }

    @Test
    void testZipCodeGetterAndSetter() {
        address.setZipCode("62704");
        assertEquals("62704", address.getZipCode());
    }

    @Test
    void testGetAddressInfo_AllFieldsSet() {
        address.setStreetAddress1("123 Main St");
        address.setStreetAddress2("Apt 4B");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62704");

        String expected = "123 Main St Apt 4B, Springfield, IL 62704";
        assertEquals(expected, address.getAddressInfo());
    }

    @Test
    void testGetAddressInfo_EmptyFields() {
        String expected = "null, null, null null";
        assertEquals(expected, address.getAddressInfo());
    }

    @Test
    void testGetAddressInfo_PartialFieldsSet() {
        address.setStreetAddress1("456 Elm St");
        address.setCity("Chicago");
        address.setZipCode("60601");

        // StreetAddress2, State are null
        String expected = "456 Elm St, Chicago, null 60601";
        assertEquals(expected, address.getAddressInfo());
    }

    @Test
    void testEqualsAndHashCode() {
        Address a1 = Address.builder().build();
        a1.setStreetAddress1("123 Main St");
        a1.setCity("A");
        a1.setState("IL");
        a1.setZipCode("12345");
        a1.setStreetAddress2("Apt 1");

        Address a2 = Address.builder().build();
        a2.setStreetAddress1("123 Main St");
        a2.setCity("A");
        a2.setState("IL");
        a2.setZipCode("12345");
        a2.setStreetAddress2("Apt 1");

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testNotEqualsDifferentTypeOrNull() {
        Address a1 = Address.builder().build();
        a1.setCity("CityX");

        assertNotEquals(a1, null);
        assertNotEquals(a1, "not an address");
    }

    @Test
    void testEqualsSameObject() {
        Address a1 = Address.builder().build();
        a1.setCity("CityX");
        assertEquals(a1, a1);
    }

    @Test
    void testNotEqualsDifferentStreetAddress1() {
        Address a1 = Address.builder().build();
        a1.setStreetAddress1("123 Main St");
        a1.setCity("City");

        Address a2 = Address.builder().build();
        a2.setStreetAddress1("456 Main St");
        a2.setCity("City");

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsDifferentStreetAddress2() {
        Address a1 = Address.builder().build();
        a1.setStreetAddress2("Apt 1");
        a1.setCity("City");

        Address a2 = Address.builder().build();
        a2.setStreetAddress2("Apt 2");
        a2.setCity("City");

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsDifferentCity() {
        Address a1 = Address.builder().build();
        a1.setCity("City1");

        Address a2 = Address.builder().build();
        a2.setCity("City2");

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsDifferentState() {
        Address a1 = Address.builder().build();
        a1.setState("IL");

        Address a2 = Address.builder().build();
        a2.setState("CA");

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsDifferentZipCode() {
        Address a1 = Address.builder().build();
        a1.setZipCode("12345");

        Address a2 = Address.builder().build();
        a2.setZipCode("67890");

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsNullCity() {
        Address a1 = Address.builder().build();
        a1.setCity("City");

        Address a2 = Address.builder().build();
        // city null

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsNullCityReversed() {
        Address a1 = Address.builder().build();
        // city null

        Address a2 = Address.builder().build();
        a2.setCity("City");

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsNullState() {
        Address a1 = Address.builder().build();
        a1.setState("IL");

        Address a2 = Address.builder().build();
        // state null

        assertNotEquals(a1, a2);
    }

    @Test
    void testNotEqualsNullStateReversed() {
        Address a1 = Address.builder().build();
        // state null

        Address a2 = Address.builder().build();
        a2.setState("IL");

        assertNotEquals(a1, a2);
    }
    
    @Test
    void addressBuilderStreetAddress2() {
        Address a = Address.builder().streetAddress1("1 Main").streetAddress2("Apt 2").city("Town").state("ST").zipCode("12345").build();
        assertEquals(" Apt 2, ", a.getStreetAddress2());
    }
}