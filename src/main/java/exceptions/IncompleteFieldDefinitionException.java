package exceptions;

/**
 * The field definition is incomplete. Most probable it is missing the 'name' parameter
 */
public class IncompleteFieldDefinitionException extends Exception
{
    public IncompleteFieldDefinitionException(String message)
    {
        super(message);
    }
}
