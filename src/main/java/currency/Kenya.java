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

import static core.utils.MoneyUtils.isTrivial;

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

        HashMap<String, String> taxCertificate = new HashMap<>();
        taxCertificate.put("name", "taxCertificate");
        taxCertificate.put("label", "KRA PIN Certificate");

        fields.add(accountNumber);
        fields.add(taxCertificate);
        fields.add(accountHolderName);

        return fields;
    }

    /*
     * We have to validate all the essential fields
     * The syntax is validate_<fieldName>()
     * The method returns a HashMap of the validation of the values.
     * If you add a validation method of a non-essential field, it implies
     * that the validation is an optional field. For example, you will see
     * that there is a validate_BIC() method, yet there is no BIC defined as an
     * essential field. This means that BIC is an optional field whose value
     * will be accepted
     */

    public HashMap<?, ?> validate_accountHolderName()
    {
        HashMap<String, Object> validationResults = new HashMap<>();

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        String fieldName = currentMethodName.replace("validate_", "");

        for (HashMap<String, Object> value : values)
        {
            if (value.get("name").equals(fieldName))
            {
                String inputValue = (String) value.get("value");
                String label = (String) value.get("label");

                validationResults.put("label", label);
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

        for (HashMap<String, Object> value : values)
        {
            if (value.get("name").equals(fieldName))
            {
                String inputValue = (String) value.get("value");
                String label = (String) value.get("label");

                validationResults.put("label", label);
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

        for (HashMap<String, Object> value : values)
        {
            if (value.get("name").equals(fieldName))
            {
                String inputValue = (String) value.get("value");
                String label = (String) value.get("label");

                validationResults.put("label", label);

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

        for (HashMap<String, Object> value : values)
        {
            if (value.get("name").equals(fieldName))
            {
                String inputValue = (String) value.get("value");
                String label = (String) value.get("label");

                validationResults.put("label", label);

// TODO: Put here your custom validations for this field

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);
                validationResults.put("validity", true);
                validationResults.put("warningMessage", "Should be derived!");
            }
        }

        return validationResults;
    }

    //TODO: Work on this
    public HashMap<?, ?> validate_taxCertificate()
    {
        HashMap<String, Object> validationResults = new HashMap<>();

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        String fieldName = currentMethodName.replace("validate_", "");

        for (HashMap<String, Object> value : values)
        {
            if (value.get("name").equals(fieldName))
            {
                String inputValue = (String) value.get("value");
                String label = (String) value.get("label");

                validationResults.put("label", label);

// TODO: Put here your custom validations for this field

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);
                validationResults.put("warningMessage", "Will be verified by the tax authorities");
                validationResults.put("validity", true);
            }
        }

        return validationResults;
    }

}
