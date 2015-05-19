package CompilerError;
import GrammarSymbols.Token;

public class SemanticError extends CompilerError {

    public SemanticError( Type errorNumber, String message){
        super(errorNumber, message);
    }
    public static SemanticError IllegalETypeException(){
        return new SemanticError(Type.ILLEGAL_ETYPE,
                ">>>ERROR: ETYPE is of the wrong type (consider replacing this with more informative message)");

    }

    public static SemanticError VariableNotFoundException(Token varNotFound, int lineNumber, String line ){
        return new SemanticError(Type.VARIABLE_NOT_FOUND,
                ">>>ERROR:  VARIABLE: " + varNotFound.getVal() + " of type: " + varNotFound.getType() + "Has not been declared.\n" +
                        "Found on line "+ lineNumber + ": " + line);
    }
    public static SemanticError ModOperandsException(int lineNum, String line){
        return new SemanticError(Type.MOD_OPERANDS_NOT_INTS,
                ">>>ERROR: When using mod, both operands must be integers. \n Found on line " + lineNum + ": " + line);
    }
    public static SemanticError IllegalIndexException(int lineNum, String line){
        return new SemanticError(Type.ILLEGAL_INDEX,
                ">>>ERROR: attemped array access with illegal index. Indecies must be referenced using integer numbers.\n" +
                        "Found on line " + lineNum + ": " + line);
    }
    //public static SemanticError Illegal
    public static SemanticError GenericError(){
        return new SemanticError(Type.TEST,
                "GENERIC ERROR, MUST REPLACE");
    }

}
