package exceptions;

/**
 * There is a clash in field definitions. An optional field has been defined
 * bearing the same name as an essential field
 */
public class FieldClashException extends Exception
{
    public FieldClashException(String message)
    {
        super(message);
    }
}
