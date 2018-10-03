package core;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static core.LockSmith.PRIVATE_KEY;
import static core.LockSmith.PUBLIC_KEY;

public abstract class Money
{
    /**
     * Basic attributes of any currency implementation
     */
    public abstract String getCountryName();

    public abstract String getCountryCode();

    public abstract String getCurrencyName();

    public abstract String getCurrencyCode();

    private Enigma enigma = new Enigma();

    private LockSmith lockSmith = new LockSmith();

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
     * @param values The values of the essential fields as a HashMap
     * @throws Exception
     */
    public Money(HashMap<String, String> values) throws Exception
    {
        /**
         * Check if all the validators exist
         */
        checkValidators();

        /**
         * Check if all the necessary essentialFields() have a value
         */
        checkValues(values);

        /**
         * Set the values that were entered while initializing into the fields
         */
        injectFields(values);
    }

    /**
     * Checks that all the mandatory fields that have been defined in the currency
     * have a value attributed to them upon initialization
     */
    private void checkValues(HashMap<String, ?> values) throws Exception
    {
        for (Object field : essentialFields())
        {
            String fieldName = (String) ((HashMap<String, Object>) field).get("name");
            String userValue = (String) values.get(fieldName);

            /**
             * Throw an exception if any of the mandatory fields does not have a value
             */
            if (userValue == null)
            {
                throw new Exception(String.format("Essential field '%s' must have a value", fieldName));
            }
        }
    }

    /**
     * Sets the values into the fields
     */
    private void injectFields(HashMap<String, ?> values) throws Exception
    {
        ArrayList injectedFields = new ArrayList();

        /**
         * Get all the essential fields
         */
        ArrayList fields = essentialFields();

        for (Object field : fields)
        {
            // Object is a HashMap
            HashMap<String, Object> essentialField = (HashMap<String, Object>) field;

            /**
             * Get the name of the field
             */
            String fieldName = (String) essentialField.get("name");

            // This is a redundant check!
            if (((HashMap<String, Object>) field).containsKey(fieldName))
            {
                throw new Exception(
                        String.format("Cannot add %s as an optional field because it" +
                                        " is already defined as an essential field",
                                fieldName
                        )
                );
            }

            /**
             * Get the value of this field name as defined from the initialization parameters
             */
            String fieldValue = (String) values.get(fieldName);

            /**
             * Set the value by adding a new field onto the array
             */
            essentialField.put("value", fieldValue);

            /**
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
                throw new Exception(
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
     * Each of the essentialFields must have a way to validateValues its value. This is
     * defined in the 'validator' parameter in each of the field. The exact
     * validator methods need to be defined in each respective Currency class.
     */
    private void checkValidators() throws Exception
    {
        ArrayList fields = essentialFields();

        for (Object field : fields)
        {

            /**
             * Ensure that each field has a 'name' attribute
             */
            String fieldName = (String) ((HashMap<String, Object>) field).get("name");

            if (fieldName == null)
            {
                throw new Exception("The 'name' attribute of one or more fields has not been defined");
            }

            /**
             * Make necessary a validator method based on the name of the field
             *
             * For instance, if the name of the field is "accountHolderName", then the
             * concrete class must have a definition of "validate_accountHolderName"
             */
            String validatorMethod = String.format("validate_%s", fieldName);

            /**
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
     * Checks all the essential fields, returning an ArrayList of all the validation results
     *
     * @return An arraylist of all validation errors whenever applicable
     */
    public ArrayList validateValues() throws Exception
    {
        ArrayList validation = new ArrayList();

        for (Object field : values)
        {
            String fieldName = (String) ((HashMap<String, Object>) field).get("name");

            String validatorMethod = String.format("validate_%s", fieldName);

            Method method = this.getClass().getDeclaredMethod(validatorMethod);

            validation.add(method.invoke(this));
        }

        return validation;
    }

    /**
     * Checks all the essential fields, returning an ArrayList of all the validation results
     *
     * @return
     */
    public String saveCoin() throws Exception
    {
        ArrayList validatedFields = validateValues();

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

                throw new Exception(
                        String.format("Could not validate input %s: %s",
                                fieldName, errorMessage)
                );
            }
        }

        /**
         * If we reach this point of the code, all the fields have values
         * and they have all been validated OK. We now need to save the values and return an ID
         * of the saved data.
         *
         * The key of this value will be the hash of the input values
         */
        String valuesAsJSON = new Gson().toJson(values);

        String storageKey = Codex.encode(Codex.sha256(Codex.encode(String.format("%s.%s", getCountryName(), valuesAsJSON))));

        // TODO: ("Encode these or encrypt them otherwise because GDPR");

        HashMap<String, String> coin = new HashMap<>();
        coin.put("coinId", storageKey);

        validatedFields.add(coin);

        String payload = new Gson().toJson(validatedFields);

        String encrypted_msg = enigma.encryptText(payload, enigma.getPrivate(PRIVATE_KEY));

        Store store = new Store();

        if (store.saveCoin(storageKey, encrypted_msg))
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
     * Retrieves a coin from the store and displays it
     *
     * @return
     */
    public String showCoin(String coinId)
    {
        String coin = null;

        try
        {
            Store store = new Store();

            coin = enigma.decryptText((String) store.displayCoin(coinId), enigma.getPublic(PUBLIC_KEY));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return coin;
    }

}
