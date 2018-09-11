package test;

import core.EnDec;
import currency.Kenya;

import java.util.HashMap;

public class CreateCoin
{
    public static void main(String[] args) throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountHolderAddress", "Hello world");
        values.put("accountNumber", "0100300359700");
        values.put("BIC", "SCBKENLXXXX");

        Kenya kenya = new Kenya(values);

        String validity = kenya.validateValues().toString();
        System.out.println(validity);


        String coinID = kenya.save();
        System.out.println(coinID);

    }
}
