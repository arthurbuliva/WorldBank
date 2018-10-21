package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.CoinDisplay;
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
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
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
     * curl -X POST -H "Content-type: application/json" -d
     * "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",
     * \"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",
     * \"BIC\": \"SCBKENLXXXX\"}"
     * localhost:8080/validate
     * @throws InvocationTargetException   Exception when calling the necessary method for validation.storage
     * @throws IllegalAccessException      Should not access the class being invoked
     * @throws UndefinedValidatorException The validator method for a field has not been defined
     * @throws NoSuchMethodException       When invoking the necessary Money class
     * @throws InstantiationException      When trying to initialize the necessary Money implementation
     * @throws ClassNotFoundException      When trying to initialize the necessary Money implementation
     * @throws IOException                 General IO exception when reading the encryption and/or decryption key files
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
     * Save input data
     *
     * @param data The input data that is to be saved
     * @return A map of the status of the input values including the coin id upon successful save
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
     * curl -X POST -H "Content-type: application/json" -d
     * "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",
     * \"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",
     * \"BIC\": \"SCBKENLXXXX\"}"
     * localhost:8080/save
     * @throws InvocationTargetException   Exception when calling the necessary method for validation.storage
     * @throws IllegalAccessException      Should not access the class being invoked
     * @throws UndefinedValidatorException The validator method for a field has not been defined
     * @throws NoSuchMethodException       When invoking the necessary Money class
     * @throws InstantiationException      When trying to initialize the necessary Money implementation
     * @throws ClassNotFoundException      When trying to initialize the necessary Money implementation
     * @throws BadPaddingException         Bad padding size during encoding/decoding
     * @throws FieldValidationException    An exception during validating the input values of a field
     * @throws IllegalBlockSizeException   Bad block size during encoding/decoding
     * @throws StorageEncodingException    An exception occurred during the encoding process
     * @throws InvalidKeyException         Invalid encryption/decryption key
     * @throws InvalidKeySpecException     Invalid encryption/decryption key specification
     * @throws IOException                 General IO exception when reading the encryption and/or decryption key files
     * @throws NoSuchPaddingException      Exception in the data in the encryption and/or decryption key files
     * @throws NoSuchAlgorithmException    No such algorithm provided for encryption and/or decryption
     * @throws NoSuchProviderException     Invalid encryption/decryption with regards to the encryption and/or decryption key files
     */
    @RequestMapping("/save")
    public String mintCoin(@RequestBody String data)
            throws InvocationTargetException, IllegalAccessException, UndefinedValidatorException,
            IOException, NoSuchMethodException, InstantiationException, ClassNotFoundException, BadPaddingException,
            NoSuchAlgorithmException, FieldValidationException, IllegalBlockSizeException, StorageEncodingException,
            InvalidKeyException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException
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

        ArrayList<HashMap> validated = gson.fromJson(validity, listType);

        HashMap<String, String> coinData = new HashMap<>();

        for (Object values : validated)
        {
            HashMap inputValues = (HashMap) values;

            Boolean isValid = (Boolean) inputValues.get("validity");

            // No need to proceed if any of these values is not valid
            if (!isValid)
            {
                return validity;
            }

            coinData.put((String) inputValues.get("field"), (String) inputValues.get("inputValue"));
        }

        // Dynamically determine the appropriate Money instance to invoke the validation against
        Constructor<?> constructor = Class.forName(String.format("currency.%s", countryName))
                .getConstructor(HashMap.class);
        Object instance = constructor.newInstance(coinData);

        Money money = (Money) instance;

        CoinDisplay display = new CoinDisplay();
        return display.showCoin(money.saveCoin());
    }

    /**
     * Display the values of a coin of a given coin id
     *
     * @param data The JSON data containing the coin id
     *             <p>
     *             Example usage:
     *             curl -X POST -H "Content-type: application/json" -d
     *             "{\"cGNDWFlETzFaUUlPYndkWTRETmJ0MTJ1aS9UQUR2OEI3SXpNOTFZV1Mvbk1tdXhkaGZCSVVJK2pSa3AwZndWMDdGTDZxRUw2S3MxeFZ0TFhVaUR3d2c\"}"
     *             localhost:8080/show
     * @return The associated input data retrieved from storage
     * @throws IOException              General IO exception when reading the encryption and/or decryption key files
     * @throws NoSuchPaddingException   Exception in the data in the encryption and/or decryption key files
     * @throws NoSuchAlgorithmException No such algorithm provided for encryption and/or decryption
     * @throws NoSuchProviderException  Invalid encryption/decryption with regards to the encryption and/or decryption key files
     */
    @RequestMapping("/show")
    public String showCoin(@RequestBody String data)
            throws
            IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException
    {
        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, String> map = mapper.readValue(data, new TypeReference<Map<String, String>>()
        {
        });

        String coinId = map.get("coinId");

        CoinDisplay display = new CoinDisplay();

        return display.showCoin(coinId);
    }
}
