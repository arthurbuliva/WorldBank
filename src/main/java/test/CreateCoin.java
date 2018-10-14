package test;

import core.Money;
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


        System.out.println(values);

        Money money = new Kenya(values);

        String coin = money.saveCoin();

        System.out.println(coin);
//        System.out.println(money.showCoin(coin));
    }
}
