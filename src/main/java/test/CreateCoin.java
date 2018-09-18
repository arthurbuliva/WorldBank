package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import currency.Kenya;

import java.util.HashMap;

public class CreateCoin
{
    public static void main(String[] args) throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountNumber", "12345678900987654321");
        values.put("accountHolderAddress", "Hello world");
        values.put("BIC", "SCBKENLXXXX");

        Kenya kenya = new Kenya(values);

        String validity = kenya.validateValues().toString();
        System.out.println(validity);


        System.out.println(kenya.save());

    }
}
