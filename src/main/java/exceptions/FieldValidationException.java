package exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An exception during validating the input values of a field
 */
public class FieldValidationException extends Exception
{
    public FieldValidationException(String message)
    {
        super(message);

        Logger.getLogger("global").log(Level.SEVERE, message);
    }
}
