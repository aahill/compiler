package CompilerError;

/**
 * Base class for errors generated by the parts of the compiler.
 */
public abstract class CompilerError extends Exception
{
    /** The type of error.  New types should be added to the enumeration
     * as the compiler generates new errors.
     */
    public enum Type {TEST, BAD_COMMENT, ILLEGAL_CHARACTER, UNTERMINATED_COMMENT,
        IDENTIFIER_TOO_LONG, CONSTANT_TOO_LONG, UNEXPECTED_TOKEN, PARSE_TABLE_ERROR,
        TOKEN_MISMATCH, VARIABLE_NOT_FOUND, ILLEGAL_ETYPE, MOD_OPERANDS_NOT_INTS, ILLEGAL_INDEX,
        ILLEGAL_TYPE_CONVERSION, PROCEDURE_PARAMETER_ERROR, ILLEGAL_PARAMETER, TOO_MANY_PARAMETERS,
        PARAMETER_MISMATCH, NOT_PROC_OR_FUNCT, CURRENT_FUNCTION_MISMATCH, NOT_ARRAY};

    /** The type of error represented by this object.  This field is declared
     * as final and must be set in the constructor.
     */
    protected final Type errorType;

    public CompilerError(Type errorType)
    {
        super("Unknown error");
        this.errorType = errorType;
    }

    public CompilerError(Type errorType, String message)
    {
        super(message);
        this.errorType = errorType;
    }

}
