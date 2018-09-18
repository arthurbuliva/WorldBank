package services;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Money;
import currency.Kenya;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;

@RestController
public class MintController
{
    @RequestMapping("/supportMatrix")
    public String supportMatrix()
    {
        List<String> countries = new ArrayList<>();
        countries.add("Kenya");

        Gson gson =
                new GsonBuilder().setPrettyPrinting().create();
//                new Gson();

        return gson.toJson(countries);
    }

    /**
     * Example usage:
     * curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/validate
     * @param data
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @RequestMapping("validate")
    public String validate(
//            @RequestParam(value = "country") String country,
            @RequestBody String data
    )
            throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> map = mapper.readValue(data, new TypeReference<Map<String, String>>(){});

        String countryName = map.get("country");
        map.remove("country");

        Money money = null;

        switch (countryName.toUpperCase())
        {
            case "KENYA":
            {
                money = new Kenya(map);
            }
            break;
            default:
            {
//                throw new Exception("Unsupported country");
            }
                break;
        }

        try
        {
            ArrayList validity = money.validateValues();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(validity);

            return json;
        }
        catch (NullPointerException ex)
        {
            HashMap error = new HashMap();

            error.put("country", countryName);
            error.put("errorMessage", String.format("%s is unsupported", countryName));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(error);

            return json;
        }
    }

    /**
     * Example usage:
     * curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/mintCoin
     * @param data
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @RequestMapping("mintcoin")
    public String mintCoin(
//            @RequestParam(value = "country") String country,
            @RequestBody String data
    )
            throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> map = mapper.readValue(data, new TypeReference<Map<String, String>>(){});

        String countryName = map.get("country");
        map.remove("country");

        Money money = null;

        switch (countryName.toUpperCase())
        {
            case "KENYA":
            {
                money = new Kenya(map);
            }
            break;
            default:
            {
//                throw new Exception("Unsupported country");
            }
                break;
        }

        try
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(money.save());

            return json;
        }
        catch (NullPointerException ex)
        {
            HashMap error = new HashMap();

            error.put("country", countryName);
            error.put("errorMessage", String.format("%s is unsupported", countryName));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(error);

            return json;
        }
    }
}
