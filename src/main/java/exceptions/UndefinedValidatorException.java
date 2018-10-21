package exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The validator method for a field has not been defined
 */
public class UndefinedValidatorException extends Exception
{
    public UndefinedValidatorException(String message)
    {
        super(message);

        Logger.getLogger("global").log(Level.SEVERE, message);
    }
}
