package exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An exception occurred during the encoding process
 */
public class StorageEncodingException extends Exception
{
    public StorageEncodingException(String message)
    {
        super(message);

        Logger.getLogger("global").log(Level.SEVERE, message);
    }
}
