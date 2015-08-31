package CompilerError;
import GrammarSymbols.*;
import LexicalAnalyzer.*;

public class ParserError extends CompilerError{

    public ParserError(Type errorNumber, String message)
    {
        super(errorNumber, message);
    }

    /**
     * error thrown if the parser table is empty due to some problem in reading the parser table file
     * @return a ParserError alerting the user that the parse table is empty
     */
    public static ParserError ParseTableErrorException(){
        return new ParserError(Type.PARSE_TABLE_ERROR,
                "ERROR: parse cannot be read, probably due to a failure in reading the parse table file");
    }
    public static ParserError UnexpectedTokenException(Token unexpected, Driver lexDriver){
        return new ParserError(Type.UNEXPECTED_TOKEN,
                "ERROR: UNEXPECTED " + unexpected.getType() + " with value: \"" + unexpected.getVal() + "\"" +
                        " found on line " + lexDriver.lexer.charStream.lineNumber() + ".\n" /*+
                        ">>> "+ lexDriver.lexer.charStream.getCurrentLine() + "\n"*/);
    }
    public static ParserError TokenMismatchException(GrammarSymbol expectedVal, Token receivedVal, Driver LexDriver){
        return new ParserError(Type.TOKEN_MISMATCH,
                "ERROR: TOKEN MISMATCH FOUND ON LINE " + LexDriver.lexer.charStream.lineNumber() + " found.\n"+
                        "Expected \"" + expectedVal + "\". Instead received \"" + receivedVal.getVal() /*+
                        "\" \n>>> " + LexDriver.lexer.charStream.getCurrentLine() + "\n"*/ );
    }
    /*public static ParserError PanicModeFailureException(){
        return new ParserError((Type))
    }*/
}
