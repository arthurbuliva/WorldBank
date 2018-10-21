package exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The field definition is incomplete. Most probable it is missing the 'name' parameter
 */
public class IncompleteFieldDefinitionException extends Exception
{
    public IncompleteFieldDefinitionException(String message)
    {
        super(message);

        Logger.getLogger("global").log(Level.SEVERE, message);
    }
}
