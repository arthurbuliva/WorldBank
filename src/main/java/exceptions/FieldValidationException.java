package exceptions;

/**
 * An exception during validating the input values of a field
 */
public class FieldValidationException extends Exception
{
    public FieldValidationException(String message)
    {
        super(message);
    }
}
