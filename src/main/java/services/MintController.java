package services;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import core.Money;
import core.SupportMatrix;
import exceptions.FieldValidationException;
import exceptions.StorageEncodingException;
import exceptions.UndefinedValidatorException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     */
    @RequestMapping("/validate")
    public String validate(@RequestBody String data) throws
            IOException, IllegalAccessException, UndefinedValidatorException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException
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

        // Dynamically determine the appropriate Money instance to invoke the validation against
        Constructor<?> constructor = Class.forName(String.format("currency.%s", countryName))
                .getConstructor(HashMap.class);
        Object instance = constructor.newInstance(map);

        Money money = (Money) instance;

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
    public String mintCoin(@RequestBody String data)
            throws InvocationTargetException, IllegalAccessException, UndefinedValidatorException,
            IOException, NoSuchMethodException, InstantiationException, ClassNotFoundException, BadPaddingException,
            NoSuchAlgorithmException, FieldValidationException, IllegalBlockSizeException, StorageEncodingException,
            InvalidKeyException, InvalidKeySpecException
    {
        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, String> map = mapper.readValue(data, new TypeReference<Map<String, String>>()
        {
        });

        String countryName = map.get("country");

        String validity = validate(data);

        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<HashMap>>()
        {
        }.getType();

        ArrayList<HashMap> validatedDataList = gson.fromJson(validity, listType);

        HashMap<String, String> toBeSaved = new HashMap<>();

        for(Object values : validatedDataList)
        {
            HashMap inputValues = (HashMap) values;

            Boolean isValid = (Boolean) inputValues.get("validity");

            if(!isValid)
            {
                return validity;
            }

            toBeSaved.put((String) inputValues.get("field"), (String) inputValues.get("inputValue"));
        }

        System.out.println(toBeSaved);

        // Dynamically determine the appropriate Money instance to invoke the validation against
        Constructor<?> constructor = Class.forName(String.format("currency.%s", countryName))
                .getConstructor(HashMap.class);
        Object instance = constructor.newInstance(toBeSaved);

        Money money = (Money) instance;

        return money.showCoin(money.saveCoin());
    }
}
