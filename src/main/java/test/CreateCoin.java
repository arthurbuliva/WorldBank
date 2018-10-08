package test;

import currency.Kenya;

import java.util.HashMap;

public class CreateCoin
{
    public static void main(String[] args) throws Exception
    {
        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Mr Buliva!");
        values.put("accountNumber", "411234567890");
        values.put("accountHolderAddress", "Amsterdam, The Netherlands");
        values.put("BIC", "ABNANL2A");

        Kenya kenya = new Kenya(values);

        String coin = kenya.saveCoin();

        System.out.println(coin);
        System.out.println(kenya.showCoin(coin));
    }
}
