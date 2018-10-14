package services;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Money;
import core.SupportMatrix;
import currency.Kenya;
import exceptions.FieldClashException;
import exceptions.IncompleteFieldDefinitionException;
import exceptions.InvalidInputException;
import exceptions.UndefinedValidatorException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

@RestController
public class MintController
{
    /**
     * Displays the countries that are currently supported
     *
     * @return The list of supported countries
     */
    @RequestMapping("/supportMatrix")
    public String supportMatrix()
    {
        Gson gson =
                new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(SupportMatrix.supportMatrix());
    }

    /**
     * Validate input data
     *
     * @param data The input data that is to be validated
     * @return A map of the validation status of the input values
     * <p>
     * Example input:
     * {
     * "country": "Kenya",
     * "accountHolderAddress": "Hello world",
     * "accountNumber": "1234567S8900987654321",
     * "accountHolderName": "Arthur Buliva",
     * "BIC": "SCBKENLXXXX"
     * }
     * <p>
     * <p>
     * Example output:
     * [
     * {
     * "field": "accountNumber",
     * "inputValue": "1234567S8900987654321",
     * "errorMessage": "Enter a valid account number",
     * "validity": false
     * },
     * {
     * "field": "accountHolderName",
     * "inputValue": "Arthur Buliva",
     * "validity": true
     * },
     * {
     * "field": "accountHolderAddress",
     * "inputValue": "Hello world",
     * "validity": true
     * },
     * {
     * "field": "BIC",
     * "inputValue": "SCBKENLXXXX",
     * "warningMessage": "Should be derived!",
     * "validity": true
     * }
     * ]
     * <p>
     * <p>
     * How to test using curl:
     * curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/validate
     * @throws IOException
     * @throws IllegalAccessException
     * @throws UndefinedValidatorException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InvalidInputException
     * @throws NoSuchPaddingException
     * @throws IncompleteFieldDefinitionException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws FieldClashException
     */
    @RequestMapping("/validate")
    public String validate(@RequestBody String data) throws
            IOException, IllegalAccessException, UndefinedValidatorException,
            InvocationTargetException, NoSuchMethodException, InvalidInputException,
            NoSuchPaddingException, IncompleteFieldDefinitionException, NoSuchAlgorithmException,
            NoSuchProviderException, FieldClashException
    {
        ArrayList supportMatrix = SupportMatrix.supportMatrix();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, String> map = mapper.readValue(data, new TypeReference<Map<String, String>>()
        {
        });

        String countryName = map.get("country");

        if (countryName == null) // No country specified
        {
            HashMap<String, String> missingCountry = new HashMap<>();

            missingCountry.put(
                    "errorMessage",
                    String.format(
                            "No country has been specified. Supported countries are %s",
                            supportMatrix
                    )
            );

            return gson.toJson(missingCountry);
        }

        if (!supportMatrix.contains(countryName)) // Country is not supported
        {
            HashMap<String, String> unsupportedCountry = new HashMap<>();

            unsupportedCountry.put("country", countryName);
            unsupportedCountry.put(
                    "errorMessage",
                    String.format(
                            "%s is unsupported. Supported countries are %s",
                            countryName, supportMatrix
                    )
            );

            return gson.toJson(unsupportedCountry);
        }

        map.remove("country");

        Money money = null;

        // TODO: Make this list dynamic
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

        return gson.toJson(money.validateValues());

    }

    /**
     * Example usage:
     * curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/mintCoin
     *
     * @param data
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @RequestMapping("/save")
    public String mintCoin(@RequestBody String data) throws InvocationTargetException, IllegalAccessException, UndefinedValidatorException, IOException, NoSuchMethodException, InvalidInputException, NoSuchPaddingException, IncompleteFieldDefinitionException, NoSuchAlgorithmException, NoSuchProviderException, FieldClashException
    {
        return validate(data);

        //convert json to array list
        // Then crawl checking if there is an error

//        ObjectMapper mapper = new ObjectMapper();
//        HashMap<String, String> map = mapper.readValue(data, new TypeReference<Map<String, String>>()
//        {
//        });
//
//        String countryName = map.get("country");
//        map.remove("country");
//
//        Money money = null;
//
//        switch (countryName.toUpperCase())
//        {
//            case "KENYA":
//            {
//                money = new Kenya(map);
//            }
//            break;
//            default:
//            {
////                throw new Exception("Unsupported country");
//            }
//            break;
//        }
//
//        try
//        {
//            //TODO: What if before attempting to save we first validate? This way we do prior checks before proceeding
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            String json = gson.toJson(money.showCoin(money.saveCoin()));
//
//            return json;
//        }
//        catch (NullPointerException ex)
//        {
//            HashMap error = new HashMap();
//
//            error.put("country", countryName);
//            error.put("errorMessage", String.format("%s is unsupported", countryName));
//
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            String json = gson.toJson(error);
//
//            return json;
//        }
    }
}
