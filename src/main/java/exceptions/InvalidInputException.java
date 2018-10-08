package exceptions;

/**
 * There is input which was not expected. No essential nor optional fields have been defined therefor
 */
public class InvalidInputException extends Exception
{
    public InvalidInputException(String message)
    {
        super(message);
    }
}
