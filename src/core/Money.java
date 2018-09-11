package core;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
     * FieldTypes that are optional
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
    public abstract ArrayList<HashMap<String, Object>> optionalFields();

    /**
     * Values of the essential fields
     */
    protected ArrayList values;

    /**
     * Constructor of the Money class
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
    private void injectFields(HashMap<String, ?> values)
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

            /**
             * Get the value of this field name as defined from the initialization parameters
             */
            String fieldValue = (String) values.get(fieldName);

            /**
             * Set the value by adding a new field onto the array
             */
            essentialField.put("value", fieldValue);


            injectedFields.add(essentialField);
        }













        /**
         * TODO: Next, inject all the optional fields
         */
//        ArrayList fields = essentialFields();
//
//        for (Object field : fields)
//        {
//            // Object is a HashMap
//            HashMap<String, Object> essentialField = (HashMap<String, Object>) field;
//
//            /**
//             * Get the name of the field
//             */
//            String fieldName = (String) essentialField.get("name");
//
//            /**
//             * Get the value of this field name as defined from the initialization parameters
//             */
//            String fieldValue = (String) values.get(fieldName);
//
//            /**
//             * Set the value by adding a new field onto the array
//             */
//            essentialField.put("value", fieldValue);
//
//
//            injectedFields.add(essentialField);
//        }























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
     * @return An arraylist of all validation errors whenever applicable
     */
    public ArrayList validateValues() throws Exception
    {
        ArrayList validation = new ArrayList();

        ArrayList fields = essentialFields();

        for (Object field : fields)
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
     * @return
     */
    public String save() throws Exception
    {
        ArrayList validatedFields = validateValues();

        for (Object field : validatedFields)
        {
            String fieldName = (String) ((HashMap<String, Object>) field).get("field");
            String errorMessage = (String) ((HashMap<String, Object>) field).get("errorMessage");
            boolean validity = (boolean) ((HashMap<String, Object>) field).get("validity");

            if(errorMessage != null)
            {
                throw new Exception(errorMessage);
            }
            if(!validity)
            {
                throw new Exception("Could not validate input on field " + fieldName);
            }
        }

        /**
         * If we reach this point of the code, all the essential fields have values
         * and they have all been validated OK. We now need to save the values and return an ID
         * of the saved data.
         *
         * The key of this value will be the hash of the input values
         */

        String currencyName = getClass().getName();
        String valuesAsJSON = new Gson().toJson(values);

        // TODO: ("Encode these or encrypt them otherwise");

//        Logger.getLogger(this.getClass().getName()).log(
//                Level.INFO,
//                String.format("%d%s => %s", currentTimeStamp, currencyName, valuesAsJSON)
//        );

        return EnDec.sha256(EnDec.encode(String.format("%s %s", currencyName, valuesAsJSON)));
    }

}
