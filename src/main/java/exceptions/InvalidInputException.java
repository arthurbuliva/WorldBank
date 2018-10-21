package exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * There is input which was not expected. No essential nor optional fields have been defined therefor
 */
public class InvalidInputException extends Exception
{
    public InvalidInputException(String message)
    {
        super(message);

        Logger.getLogger("global").log(Level.SEVERE, message);
    }
}
