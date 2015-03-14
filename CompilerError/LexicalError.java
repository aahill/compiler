package CompilerError;

/** Exception class thrown when a lexical error is encountered. */
public class LexicalError extends CompilerError
{
    public LexicalError(Type errorNumber, String message)
    {
        super(errorNumber, message);
    }

    // Factory methods to generate the lexical exception types.

    public static LexicalError BadComment(int lineNum)
    {
        return new LexicalError(Type.BAD_COMMENT,
                ">>> ERROR: Cannont include { inside a comment."+
                        "Found on line"+ lineNum);
    }

    public static LexicalError IllegalCharacter(char c, int lineNum)
    {
        return new LexicalError(Type.ILLEGAL_CHARACTER,
                ">>> ERROR: Illegal character: " + c + "\n" +
                        "Found on line"+ lineNum);
    }

    public static LexicalError UnterminatedComment(int lineNum)
    {
        return new LexicalError(Type.UNTERMINATED_COMMENT,
                ">>> ERROR: Unterminated comment." + "\n" +
                        "Found on line" + lineNum);
    }
    /**
     * error thrown if identifier is longer than some specified maximum length
     * @param lineNum the line number to be displayed in the stack
     * @param id the identifier that is loner than the specified max length
     * @return a lexical error
     */
    public static LexicalError IdentiferTooLong(int lineNum, String id){
        return new LexicalError(Type.IDENTIFIER_TOO_LONG,
                ">>> ERROR: Identifier: "+ id + "... too long." + "\n"
                        + "Found on line " + lineNum);
    }
    /**
     * error thrown if identifier is longer than some specified maximum length
     * @param lineNum the line number to be displayed in the stack
     * @param id the identifier that is loner than the specified max length
     * @return a lexical error
     */
    public static LexicalError ConstantTooLong(int lineNum, String id){
        return new LexicalError(Type.CONSTANT_TOO_LONG,
                ">>> ERROR: Constant: "+ id + "... too long." + "\n"
                        + "Found on line " + lineNum);
    }
}