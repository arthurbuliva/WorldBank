package exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * There is a clash in field definitions. An optional field has been defined
 * bearing the same name as an essential field
 */
public class FieldClashException extends Exception
{
    public FieldClashException(String message)
    {
        super(message);

        Logger.getLogger("global").log(Level.SEVERE, message);
    }
}
