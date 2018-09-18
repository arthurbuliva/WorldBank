package test;

import currency.Kenya;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class KenyaTest
{

    @org.junit.jupiter.api.Test
    void mintTest() throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountHolderAddress", "Herengracht 597, 1069RE, Amsterdam");

        String expectedValue="Kenya";

        Kenya kenya = new Kenya(values);
        assertEquals(expectedValue, kenya.getCountryName());
    }

    @Test
    void validationFields() throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountHolderAddress", "Herengracht 597, 1069RE, Amsterdam");

        Kenya kenya = new Kenya(values);
        System.out.println(kenya.essentialFields());
    }
}