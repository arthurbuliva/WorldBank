package currency;

import core.Money;

import java.util.ArrayList;
import java.util.HashMap;

public class Kenya extends Money
{
    /**
     * Once you set these values, and the class starts to be in use,
     * DO NOT and I repeat DO NOT modify the values because the encryption
     * upon storage depends on them.
     * Changing these values will mean that you cannot retrieve the
     * values from storage
     */
    private final String COUNTRY = "Kenya";
    private final String COUNTRY_CODE = "KE";
    private final String CURRENCY_NAME = "Kenya Shilling";
    private final String CURRENCY_CODE = "KES";
    /**
     * Just in case you missed it,
     * DO NOT modify these values once they have started being used
     */

    public Kenya(HashMap<String, String> values) throws Exception
    {
        super(values);
    }

    @Override
    public String getCountryName()
    {
        return COUNTRY;
    }

    @Override
    public String getCountryCode()
    {
        return COUNTRY_CODE;
    }

    @Override
    public String getCurrencyName()
    {
        return CURRENCY_NAME;
    }

    @Override
    public String getCurrencyCode()
    {
        return CURRENCY_CODE;
    }

    @Override
    public ArrayList essentialFields()
    {
        ArrayList fields = new ArrayList();

        HashMap<String, Object> accountNumber = new HashMap();
        accountNumber.put("name", "accountNumber");
        accountNumber.put("label", "Account Number");

        HashMap<String, Object> accountHolderName = new HashMap();
        accountHolderName.put("name", "accountHolderName");
        accountHolderName.put("label", "Account Holder Name");

        fields.add(accountNumber);
        fields.add(accountHolderName);

        return fields;
    }

    /**
     * FieldTypes that should not be input but are derived from the other fields
     */
    @Override
    public ArrayList<HashMap<String, Object>> derivedFields()
    {
        return null;
    }

    public HashMap<?, ?> validate_accountHolderName()
    {
        HashMap validationResults = new HashMap<>();

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        String fieldName = currentMethodName.replace("validate_", "");

        for (Object value : values)
        {
            HashMap<String, Object> essentialField = (HashMap<String, Object>) value;

            if (essentialField.get("name").equals(fieldName))
            {
                String inputValue = (String) essentialField.get("value");

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);

// TODO: Put here your custom validations for this field

                if(inputValue.length() < 3)
                {
                    validationResults.put("validity", false);
                    validationResults.put("errorMessage", "Must be at least 3 characters long");
                }
                else
                {
                    validationResults.put("validity", true);
                }
            }
        }

        return validationResults;
    }

    public HashMap<?, ?> validate_accountNumber()
    {
        HashMap validationResults = new HashMap<>();

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        String fieldName = currentMethodName.replace("validate_", "");

        for (Object value : values)
        {
            HashMap<String, Object> essentialField = (HashMap<String, Object>) value;

            if (essentialField.get("name").equals(fieldName))
            {
                String inputValue = (String) essentialField.get("value");

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);

// TODO: Put here your custom validations for this field

                String regex = "\\d+";

                if(!inputValue.matches(regex))
                {
                    validationResults.put("validity", false);
                    validationResults.put("errorMessage", "Enter a valid account number");
                }
                else
                {
                    validationResults.put("validity", true);
                }


            }
        }

        return validationResults;
    }

    public HashMap<?, ?> validate_accountHolderAddress()
    {
        HashMap validationResults = new HashMap<>();

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        String fieldName = currentMethodName.replace("validate_", "");

        for (Object value : values)
        {
            HashMap<String, Object> field = (HashMap<String, Object>) value;

            if (field.get("name").equals(fieldName))
            {
                String inputValue = (String) field.get("value");

// TODO: Put here your custom validations for this field

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);
                validationResults.put("validity", true);
            }
        }

        return validationResults;
    }

    public HashMap<?, ?> validate_BIC()
    {
        HashMap validationResults = new HashMap<>();

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        String fieldName = currentMethodName.replace("validate_", "");

        for (Object value : values)
        {
            HashMap<String, Object> field = (HashMap<String, Object>) value;

            if (field.get("name").equals(fieldName))
            {
                String inputValue = (String) field.get("value");

// TODO: Put here your custom validations for this field

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);
                validationResults.put("validity", true);
                validationResults.put("warningMessage", "Should be derived!");
            }
        }

        return validationResults;
    }

}
