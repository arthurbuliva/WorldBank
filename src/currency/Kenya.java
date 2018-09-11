package currency;

import core.Money;

import java.util.ArrayList;
import java.util.HashMap;

public class Kenya extends Money
{
    private final String COUNTRY = "Kenya";
    private final String COUNTRY_CODE = "KE";
    private final String CURRENCY_NAME = "Kenya Shilling";
    private final String CURRENCY_CODE = "KES";

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

        HashMap<String, Object> accountHolderAddress = new HashMap();
        accountHolderAddress.put("name", "accountHolderAddress");
        accountHolderAddress.put("label", "Account Holder Address");

        fields.add(accountNumber);
        fields.add(accountHolderName);
        fields.add(accountHolderAddress);

        return fields;
    }

    @Override
    public ArrayList<HashMap<String, Object>> optionalFields()
    {
        ArrayList fields = new ArrayList();

        HashMap<String, Object> swiftCode = new HashMap();
        swiftCode.put("name", "accountHolderAddress");
        swiftCode.put("label", "BIC/SWIFT Code");

        fields.add(swiftCode);

        return fields;
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

// TODO: Put here your custom validations for this field

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);
                validationResults.put("validity", true);
//                validationResults.put("errorMessage", "Dummy Message");
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
            HashMap<String, Object> essentialField = (HashMap<String, Object>) value;

            if (essentialField.get("name").equals(fieldName))
            {
                String inputValue = (String) essentialField.get("value");

// TODO: Put here your custom validations for this field
                if(inputValue.equals("Hello world"))
                {
                    validationResults.put("validity", true);
                }

                validationResults.put("field", fieldName);
                validationResults.put("inputValue", inputValue);

            }
        }

        return validationResults;
    }

    public HashMap<?, ?> validate_accountNumber()
    {
        return validate_accountHolderAddress();
    }
}
