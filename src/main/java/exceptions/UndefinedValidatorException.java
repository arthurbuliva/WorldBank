package exceptions;

/**
 * The validator method for a field has not been defined
 */
public class UndefinedValidatorException extends Exception
{
    public UndefinedValidatorException(String message)
    {
        super(message);
    }
}
