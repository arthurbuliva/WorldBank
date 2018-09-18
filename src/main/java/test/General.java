package test;

import core.Money;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Set;

public class General
{
    public static void main(String[] args) throws Exception
    {

        Class country = "Kenya".getClass();

        Constructor[] ctors = country.getDeclaredConstructors();

//
        for (Constructor ctorss : ctors) {
            Class<?>[] pType  = ctorss.getParameterTypes();
            System.out.println(ctorss);
            System.out.println(pType);
        }

        Constructor ctor = ("currency." + "kenya").getClass().getDeclaredConstructor(HashMap.class);
        ctor.setAccessible(true);

        HashMap<String, String> values = new HashMap();
        values.put("accountHolderName", "Arthur Buliva");
        values.put("accountNumber", "1234567S8900987654321");
        values.put("accountHolderAddress", "Hello world");
        values.put("BIC", "SCBKENLXXXX");

        Money money = (Money)ctor.newInstance(values);

    }
}
