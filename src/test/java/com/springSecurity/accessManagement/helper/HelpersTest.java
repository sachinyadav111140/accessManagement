package com.springSecurity.accessManagement.helper;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelpersTest {


    @Test
    void generateRandomString_generatesStringOfCorrectLength() {
        String randomString = Helpers.generateRandomString(10);
        assertEquals(10, randomString.length());
    }
    @Test
    void generateRandomString_generatesDifferentStrings() {
        String randomString1 = Helpers.generateRandomString(10);
        String randomString2 = Helpers.generateRandomString(10);
        assertNotEquals(randomString1, randomString2);
    }

    @Test
    void getFileExtension_returnsCorrectExtension() {
        assertEquals("txt", Helpers.getFileExtension("file.txt"));
        assertEquals("jpg", Helpers.getFileExtension("image.jpg"));
    }

    @Test
    void getFileExtension_returnsNullForNullInput() {
        assertNull(Helpers.getFileExtension(null));
    }

    @Test
    void capitalize_capitalizesFirstCharacter() {
        assertEquals("Hello", Helpers.capitalize("hello"));
    }



    @Test
    void updateErrorHashMap_addsNewField() {
        Map<String, List<String>> errors = new HashMap<>();
        Helpers.updateErrorHashMap(errors, "field1", "error1");
        assertTrue(errors.containsKey("field1"));
        assertEquals(1, errors.get("field1").size());
        assertEquals("error1", errors.get("field1").get(0));
    }

    @Test
    void updateErrorHashMap_updatesExistingField() {
        Map<String, List<String>> errors = new HashMap<>();
        Helpers.updateErrorHashMap(errors, "field1", "error1");
        Helpers.updateErrorHashMap(errors, "field1", "error2");
        assertEquals(2, errors.get("field1").size());
        assertEquals("error2", errors.get("field1").get(1));
    }

    @Test
    void convertStringToNumber_convertsValidString() {
        assertEquals(123, Helpers.convertStringToNumber("123"));
    }

    @Test
    void convertStringToNumber_throwsExceptionForInvalidString() {
        assertThrows(NumberFormatException.class, () -> Helpers.convertStringToNumber("abc"));
    }

}