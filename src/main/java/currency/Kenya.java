package currency;

import core.Money;
import exceptions.FieldClashException;
import exceptions.FieldValidationException;
import exceptions.IncompleteFieldDefinitionException;
import exceptions.InvalidInputException;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;

public class Kenya extends Money
{

    public Kenya(HashMap<String, String> values) throws
            NoSuchMethodException, InvalidInputException,
            NoSuchPaddingException, IncompleteFieldDefinitionException,
            NoSuchAlgorithmException, NoSuchProviderException,
            FieldClashException, FieldValidationException
    {
        super(values);
    }

    @Override
    public String getCountryName()
    {
        return "Kenya";
    }

    @Override
    public String getCountryCode()
    {
        return "KE";
    }

    @Override
    public String getCurrencyName()
    {
        return "Kenya Shilling";
    }

    @Override
    public String getCurrencyCode()
    {
        return "KES";
    }

    @Override
    public ArrayList essentialFields()
    {
        ArrayList<HashMap<String, String>> fields = new ArrayList<>();

        HashMap<String, String> accountNumber = new HashMap<>();
        accountNumber.put("name", "accountNumber");
        accountNumber.put("label", "Account Number");

        HashMap<String, String> accountHolderName = new HashMap<>();
        accountHolderName.put("name", "accountHolderName");
        accountHolderName.put("label", "Account Holder Name");

        fields.add(accountNumber);
        fields.add(accountHolderName);

        return fields;
    }

    public HashMap<?, ?> validate_accountHolderName()
    {
        HashMap<String, Object> validationResults = new HashMap<>();

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
                else if (isTrivial(inputValue))
                {
                    validationResults.put("validity", false);
                    validationResults.put("errorMessage", "Trivial value detected");
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
        HashMap<String, Object> validationResults = new HashMap<>();

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
                else if (isTrivial(inputValue))
                {
                    validationResults.put("validity", false);
                    validationResults.put("errorMessage", "Trivial value detected");
                }
                else
                {
                    // Add a derived field if you want to
                    validationResults.put("accountNumberCode", inputValue.substring(0, 4));
                    validationResults.put("validity", true);
                }
            }
        }

        return validationResults;
    }

    public HashMap<?, ?> validate_accountHolderAddress()
    {
        HashMap<String, Object> validationResults = new HashMap<>();

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
        HashMap<String, Object> validationResults = new HashMap<>();

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
