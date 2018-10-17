package core;

import com.google.gson.Gson;
import exceptions.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Money
{
    /**
     * Basic attributes of any currency implementation
     */
    public abstract String getCountryName();

    public abstract String getCountryCode();

    public abstract String getCurrencyName();

    public abstract String getCurrencyCode();

    private Enigma enigma;

    private LockSmith lockSmith;

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
    protected ArrayList values;

    /**
     * Constructor of the Money class
     *
     * @param values The values of the essential, optional and derived fields as an ArrayList of HashMaps
     * @throws NoSuchMethodException              Undefined validator methods
     * @throws IncompleteFieldDefinitionException Field definition is incomplete
     * @throws FieldClashException           Attempting to define optional field with the same name as an essential field
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
        lockSmith = new LockSmith();
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
        for (Object field : essentialFields())
        {
            String fieldName = (String) ((HashMap<String, Object>) field).get("name");
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
        ArrayList fields = essentialFields();

        for (Object field : fields)
        {
            // Object is a HashMap
            HashMap<String, Object> essentialField = (HashMap<String, Object>) field;

            /*
             * Get the name of the field
             */
            String fieldName = (String) essentialField.get("name");

            // This is a redundant check!
            if (((HashMap<String, Object>) field).containsKey(fieldName))
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
            essentialField.put("value", fieldValue);

            /*
             * Remove this from 'values' field so that we can implement the optional fields
             */

            values.remove(fieldName);

            injectedFields.add(essentialField);
        }

        /**
         * Inject the optional fields
         */
        for (HashMap.Entry<String, ?> entry : values.entrySet())
        {
            String validatorMethod = String.format("validate_%s", entry.getKey());

            try
            {
                this.getClass().getDeclaredMethod(validatorMethod, null);
            }
            catch (NoSuchMethodException ex)
            {
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
        ArrayList fields = essentialFields();

        for (Object field : fields)
        {

            /*
             * Ensure that each field has a 'name' attribute
             */
            String fieldName = (String) ((HashMap<String, Object>) field).get("name");

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
            this.getClass().getDeclaredMethod(validatorMethod, null);
        }
    }

    /**
     * Invokes the validation of the input fields
     *
     * @return An ArrayList containing the validation results
     * @throws IllegalAccessException      Validator methods defined with the incorrect accessibility
     * @throws InvocationTargetException   Validator method invocation exception
     * @throws UndefinedValidatorException Validator has not been defined
     */
    public ArrayList<HashMap<String, Object>> validateValues() throws IllegalAccessException,
            InvocationTargetException, UndefinedValidatorException
    {
        ArrayList validation = new ArrayList();

        for (Object field : values)
        {
            String fieldName = (String) ((HashMap<String, Object>) field).get("name");

            String validatorMethod = String.format("validate_%s", fieldName);

            Method method;

            try
            {
                method = this.getClass().getDeclaredMethod(validatorMethod);
                validation.add(method.invoke(this));
            }
            catch (NoSuchMethodException exception)
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
     * @throws InvocationTargetException   Validator method invocation exception
     * @throws InvalidKeySpecException     Encryption key specifications are invalid
     * @throws NoSuchAlgorithmException    Invalid encryption algorithm
     * @throws IOException                 Input/Output exception when reading the encryption keys from file
     * @throws BadPaddingException         Invalid padding in the key file
     * @throws InvalidKeyException         Encryption keys are invalid
     * @throws IllegalBlockSizeException   Invalid block size in the key file
     * @throws StorageEncodingException    Could not encode the data
     */
    public String saveCoin() throws FieldValidationException, UndefinedValidatorException, IllegalAccessException,
            InvocationTargetException, InvalidKeySpecException, NoSuchAlgorithmException,
            IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException,
            StorageEncodingException
    {
        ArrayList<HashMap<String, Object>> validatedFields = validateValues();

        for (Object field : validatedFields)
        {
            String fieldName = (String) ((HashMap<String, Object>) field).get("field");
            boolean validity = (boolean) ((HashMap<String, Object>) field).get("validity");

            try
            {
                String warningMessage = (String) ((HashMap<String, Object>) field).get("warningMessage");

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
                String errorMessage = (String) ((HashMap<String, Object>) field).get("errorMessage");

                throw new FieldValidationException(
                        String.format("Could not validate input %s: %s",
                                fieldName, errorMessage)
                );
            }
        }

        /*
         * If we reach this point of the code, all the fields have values
         * and they have all been validated OK. We now need to save the values and return an ID
         * of the saved data.
         */
        String valuesAsJSON = new Gson().toJson(values);

        /*
         * The storage key of this value will be the hash of the input values
         */
        String storageKey = Codex.encode(
                Objects.requireNonNull(Codex.sha256(
                        Codex.encode(
                                String.format("%s.%s", getCountryName(), valuesAsJSON)
                        )
                ))
        );

        HashMap<String, Object> coin = new HashMap<>();
        coin.put("coinId", storageKey);
        validatedFields.add(coin);

        /*
         * Encrypt the payload
         */
        String payload = enigma.encryptText(new Gson().toJson(validatedFields), enigma.getPrivate());

        Store store = new Store();

        if (store.saveCoin(storageKey, payload))
        {
//            return validatedFields;
            return storageKey;
        }
        else
        {
            return null;
        }
    }

    /**
     * Check if an input value is a trivial entity such as 123456 or ABCDEF
     * @param input The value to be checked
     * @return true if value is trivial, false otherwise
     */
    public boolean isTrivial(String input)
    {
        if(input.isEmpty()) return true;

        ArrayList<Integer> differences = new ArrayList<>();

        char[] inputArray = input.toCharArray();

        for (int i = 0; i < inputArray.length - 1; i++)
        {
            if(input.matches("\\d+")) // Input is a number
            {
                differences.add(inputArray[i + 1] - inputArray[i]);
            }
            else
            {
                int x = Character.getNumericValue(inputArray[i + 1]);
                int y = Character.getNumericValue(inputArray[i]);

                differences.add(x - y);
            }
        }

        boolean isTrivial = true;

        for (int i = 0; i < differences.size() - 1; i++)
        {
            if (differences.get(i) != differences.get(i + 1))
            {
                isTrivial = false;
            }
        }

        return isTrivial;
    }

    /**
     * Retrieves a coin from the store and displays its value
     *
     * @param coinId The id of the coin to be retrieved
     * @return A decrypted string of the values
     */
    public String showCoin(String coinId)
    {
        Store store = new Store();

        try
        {
            return enigma.decryptText((String) store.displayCoin(coinId), enigma.getPublic());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
