package test;

import core.CoinDisplay;
import core.Money;
import currency.Kenya;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CreateCoin
{
    public static void main(String[] args) throws Exception
    {
        CoinDisplay display = new CoinDisplay();
//        System.out.println(display.showCoin("VXhvTmd3WTByUmlLU1J0ZkxLQmFKY28xa0lCVEtPUHg2Vm9ma1h0eHVFVzFoOStaRVZUbThqNGlGQ3NjTGRvdG9JckhnRzZQTm45aGd0V0kvMlQ3Y3c"));

        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Mr Buliva!");
        values.put("accountNumber", "1234567890");
        values.put("accountHolderAddress", "Amsterdam, The Netherlands");
        values.put("taxCertificate", "d3WTByUmlLU1J0ZkxLQmF");
        values.put("BIC", "ABNANL2A");

        Money money = new Kenya(values);

        String coin = money.saveCoin();


        System.out.println(display.showCoin(coin));


    }
}
