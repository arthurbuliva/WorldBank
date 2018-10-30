package core;

import com.google.gson.Gson;
import core.utils.MoneyUtils;
import exceptions.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Money
{
    /*
     * Basic attributes of any currency implementation
     * Once you set these values, and the class starts to be in use,
     * DO NOT and I repeat DO NOT modify the values because the encryption
     * upon storage depends on them.
     * Changing these values will mean that you cannot retrieve the
     * values from storage
     */

    /**
     * The name of the country
     * @return Name of the country eg "Botswana"
     */
    public abstract String getCountryName();

    /**
     * The country code
     * @return 2-letter country code eg "BW"
     */
    public abstract String getCountryCode();

    /**
     * The name of the currency
     * @return Name of the currency eg "Botswana Pula"
     */
    public abstract String getCurrencyName();

    /**
     * The currency code
     * @return Currency code of the country eg "BWP"
     */
    public abstract String getCurrencyCode();


    private Enigma enigma;

    /*
     * Just in case you missed it,
     * DO NOT modify these values once they have started being used
     */


    /**
     * FieldTypes that we need in order to validateValues any currency
     * <p>
     * The end result will be an array of HashMap essentialFields with attributes
     * For example
     * [
     * {
     * type        => TextField,
     * label       => "Account Holder Name",
     * validator   => validateAccountHolderName()
     * },
     * {
     * type        => TextArea,
     * label       => "Account Holder Address",
     * validator   => validateAccountHolderAddress()
     * }
     * ]
     */
    public abstract ArrayList<HashMap<String, Object>> essentialFields();

    /**
     * Values of the essential fields
     */
    protected ArrayList<HashMap<String, Object>> values;

    /**
     * Constructor of the Money class
     *
     * @param values The values of the essential, optional and derived fields as an ArrayList of HashMaps
     * @throws NoSuchMethodException              Undefined validator methods
     * @throws IncompleteFieldDefinitionException Field definition is incomplete
     * @throws FieldClashException                Attempting to define optional field with the same name as an essential field
     * @throws InvalidInputException              Inputting a value that is not expected
     * @throws NoSuchPaddingException             Invalid padding in the key file
     * @throws NoSuchAlgorithmException           Invalid encryption algorithm
     * @throws NoSuchProviderException            Invalid encryption provider
     */
    public Money(HashMap<String, String> values) throws NoSuchMethodException,
            IncompleteFieldDefinitionException, FieldClashException, InvalidInputException,
            NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, FieldValidationException
    {
        /*
         * Check if all the validators exist
         */
        checkValidators();

        /*
         * Check if all the necessary essentialFields() have a value
         */
        checkValues(values);

        /*
         * Set the values that were entered while initializing into the fields
         */
        injectFields(values);

        /*
         Initialize the encryption classes
         */
        enigma = new Enigma();
    }

    /**
     * Checks that all the mandatory fields that have been defined in the specific currency class
     * have a value attributed to them upon initialization
     *
     * @param values The values that have been input
     * @throws FieldValidationException In case any of the essential fields does not have a value
     */
    private void checkValues(HashMap<String, ?> values) throws FieldValidationException
    {
        for (HashMap<String, Object> field : essentialFields())
        {
            String fieldName = (String) field.get("name");
            String userValue = (String) values.get(fieldName);

            /*
             * Throw an exception if any of the mandatory fields does not have a value
             */
            if (userValue == null)
            {
                throw new FieldValidationException(String.format("Essential field '%s' must have a value", fieldName));
            }
        }
    }

    /**
     * Sets the values into the fields
     */
    private void injectFields(HashMap<String, ?> values) throws FieldClashException, InvalidInputException
    {
        ArrayList<HashMap<String, Object>> injectedFields = new ArrayList<>();

        /*
         * Get all the essential fields
         */
        ArrayList<HashMap<String, Object>> fields = essentialFields();

        for (HashMap<String, Object> field : fields)
        {
             /*
             * Get the name of the field
             */
            String fieldName = (String) field.get("name");

            // This is a redundant check!
            if (field.containsKey(fieldName))
            {
                throw new FieldClashException(
                        String.format("Cannot add %s as an optional field because it" +
                                        " is already defined as an essential field",
                                fieldName
                        )
                );
            }

            /*
             * Get the value of this field name as defined from the initialization parameters
             */
            String fieldValue = (String) values.get(fieldName);

            /*
             * Set the value by adding a new field onto the array
             */
            field.put("value", fieldValue);

            /*
             * Remove this from 'values' field so that we can implement the optional fields
             */

            values.remove(fieldName);

            injectedFields.add(field);
        }

        /*
         * Inject the optional fields
         */
        for (HashMap.Entry<String, ?> entry : values.entrySet())
        {
            String validatorMethod = String.format("validate_%s", entry.getKey());

            try
            {
                this.getClass().getDeclaredMethod(validatorMethod);
            }
            catch (NoSuchMethodException ex)
            {
                /*
                    Validation method is missing which implies that this field is not valid.
                    Mask this from the end user
                */
                throw new InvalidInputException(
                        String.format("%s is not a valid input parameter for %s",
                                entry.getKey(), this.getClass().getSimpleName()
                        )
                );
            }

            HashMap<String, Object> optionalField = new HashMap<>();
            optionalField.put("name", entry.getKey());
            optionalField.put("value", entry.getValue());

            injectedFields.add(optionalField);
        }

        this.values = injectedFields;
    }

    /**
     *
     */
    private ArrayList<HashMap<String, Object>> staticValues()
    {
        ArrayList<HashMap<String, Object>> staticValues = new ArrayList<>();

        HashMap<String, Object> countryName = new HashMap<>();
        countryName.put("name", "currencyCode");
        countryName.put("label", "Currency Code");
        countryName.put("value", getCountryName());

        HashMap<String, Object> countryCode = new HashMap<>();
        countryCode.put("name", "countryCode");
        countryCode.put("label", "Country Code");
        countryCode.put("value", getCountryCode());

        HashMap<String, Object> currencyName = new HashMap<>();
        currencyName.put("field", "currencyName");
        currencyName.put("label", "Currency Name");
        currencyName.put("value", getCurrencyName());

        HashMap<String, Object> currencyCode = new HashMap<>();
        currencyCode.put("field", "currencyCode");
        currencyCode.put("label", "Currency Code");
        currencyCode.put("value", getCurrencyCode());

        staticValues.add(countryName);
        staticValues.add(countryCode);
        staticValues.add(currencyName);
        staticValues.add(currencyCode);

        return staticValues;

    }

    /**
     * Checks that the validators defined in essentialFields() exist
     * <p>
     * Each of the essentialFields must have a way to validateValues its input values. This is
     * defined in the 'validator' parameter in each of the field. The exact
     * validator methods need to be concretely defined in each respective Currency class.
     *
     * @throws IncompleteFieldDefinitionException Field definition is incomplete
     * @throws NoSuchMethodException              Validator methods do not exist
     */
    private void checkValidators() throws IncompleteFieldDefinitionException, NoSuchMethodException
    {
        ArrayList<HashMap<String, Object>> fields = essentialFields();

        for (HashMap<String, Object> field : fields)
        {

            /*
             * Ensure that each field has a 'name' attribute
             */
            String fieldName = (String) field.get("name");

            if (fieldName == null)
            {
                throw new IncompleteFieldDefinitionException("The 'name' attribute of one or more fields has not been defined");
            }

            /*
             * Make necessary a validator method based on the name of the field
             *
             * For instance, if the name of the field is "accountHolderName", then the
             * concrete class must have a definition of "validate_accountHolderName"
             */
            String validatorMethod = String.format("validate_%s", fieldName);

            /*
             * Checks if all the 'validator' methods have been implemented.
             * Ideally, the validator methods should return a HashMap of the validation
             * results together with an error message whenever applicable.
             * Furthermore, they need to be declared public in order for the aggregated
             * method validateValues() to work
             *
             * Throws a java.lang.NoSuchMethodException if method does not exist
             */
            this.getClass().getDeclaredMethod(validatorMethod);
        }
    }

    /**
     * Invokes the validation of the input fields
     *
     * @return An ArrayList containing the validation results
     * @throws IllegalAccessException      Validator methods defined with the incorrect accessibility
     * @throws UndefinedValidatorException Validator has not been defined
     */
    public ArrayList<HashMap<String, Object>> validateValues() throws IllegalAccessException, UndefinedValidatorException
    {
        ArrayList validation = new ArrayList();

        for (HashMap<String, Object> field : values)
        {
            String fieldName = (String) field.get("name");

            String validatorMethod = String.format("validate_%s", fieldName);

            Method method;

            try
            {
                method = this.getClass().getDeclaredMethod(validatorMethod);
                validation.add(method.invoke(this));
            }
            catch (NoSuchMethodException | java.lang.reflect.InvocationTargetException exception)
            {
                throw new UndefinedValidatorException(
                        String.format("Validation method %s has not been defined for field %s",
                                validatorMethod, fieldName)
                );
            }
        }

        return validation;
    }

    /**
     * Saves the fields into the database
     *
     * @return The id of the saved object
     * @throws FieldValidationException    Exception when validating a field
     * @throws UndefinedValidatorException Validator has not been defined
     * @throws IllegalAccessException      Validator methods defined with the incorrect accessibility
     * @throws InvalidKeySpecException     Encryption key specifications are invalid
     * @throws NoSuchAlgorithmException    Invalid encryption algorithm
     * @throws IOException                 Input/Output exception when reading the encryption keys from file
     * @throws BadPaddingException         Invalid padding in the key file
     * @throws InvalidKeyException         Encryption keys are invalid
     * @throws IllegalBlockSizeException   Invalid block size in the key file
     * @throws StorageEncodingException    Could not encode the data
     */
    public String saveCoin() throws FieldValidationException, UndefinedValidatorException, IllegalAccessException,
            InvalidKeySpecException, NoSuchAlgorithmException,
            IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException,
            StorageEncodingException
    {
        ArrayList<HashMap<String, Object>> validatedFields = validateValues();

        for (HashMap<String, Object> field : validatedFields)
        {
            String fieldName = (String) field.get("field");
            boolean validity = (boolean) field.get("validity");

            try
            {
                String warningMessage = (String) field.get("warningMessage");

                if (warningMessage != null)
                {
                    Logger.getLogger(getCountryName()).log(
                            Level.WARNING,
                            String.format("%s: %s", fieldName, warningMessage)
                    );
                }
            }
            catch (NullPointerException ex)
            {
                //Do nothing really
            }
            if (!validity)
            {
                String errorMessage = (String) field.get("errorMessage");

                throw new FieldValidationException(
                        String.format("Could not validate input %s: %s",
                                fieldName, errorMessage)
                );
            }

            // We now know that it is valid. No need to store validity message in database
            field.remove("validity");
        }

        /*
         * If we reach this point of the code, all the fields have values
         * and they have all been validated OK. We now need to save the values and return an ID
         * of the saved data.
         *
         * This ID, the storage key, will be the hash of the input values
         */
        String valuesAsJSON = new Gson().toJson(values);

        String storageKey = MoneyUtils.encode(
                Objects.requireNonNull(MoneyUtils.sha256(
                        MoneyUtils.encode(
                                String.format("%s.%s", getCountryName(), valuesAsJSON)
                        )
                ))
        );

        HashMap<String, Object> coin = new HashMap<>();
        coin.put("coinId", storageKey);
        validatedFields.addAll(staticValues()); // Add static properties of the Currency
        validatedFields.add(coin);

        //TODO: Output the labels of the fields as part of output

        /*
         * Encrypt the payload
         */
        String payload = enigma.encryptText(new Gson().toJson(validatedFields), enigma.getPrivate());

        Store store = new Store();

        if (store.saveCoin(storageKey, getCountryName(), payload))
        {
//            return validatedFields;
            return storageKey;
        }
        else
        {
            return null;
        }
    }
}
