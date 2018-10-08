package exceptions;

/**
 * An exception occurred during the encoding process
 */
public class StorageEncodingException extends Exception
{
    public StorageEncodingException(String message)
    {
        super(message);
    }
}
