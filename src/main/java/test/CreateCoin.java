package test;

import currency.Kenya;

import java.util.HashMap;

public class CreateCoin
{
    public static void main(String[] args) throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountNumber", "411234567890");
        values.put("accountHolderAddress", "Amsterdam, Netherlands");
        values.put("BIC", "SCBKENLXXXX");

        Kenya kenya = new Kenya(values);

//        String validity = kenya.validateValues().toString();
//        System.out.println(validity);


//        System.out.println();

        String coin = kenya.saveCoin();

        System.out.println(coin);

        System.out.println(kenya.showCoin(coin));

    }
}
