package test;

import currency.Kenya;

import java.util.HashMap;

public class CreateCoin
{
    public static void main(String[] args) throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountNumber", "1234567S8900987654321");
        values.put("accountHolderAddress", "Hello world");
        values.put("BIC", "SCBKENLXXXX");

        Kenya kenya = new Kenya(values);

        String validity = kenya.validateValues().toString();
        System.out.println(validity);


        String coinID = kenya.save();
        System.out.println(coinID);

    }
}
