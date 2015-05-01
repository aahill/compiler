package CompilerError;
import GrammarSymbols.Token;

public class SemanticError extends CompilerError {

    public SemanticError( Type errorNumber, String message){
        super(errorNumber, message);
    }
    public static SemanticError IllegalETypeException(){
        return new SemanticError(Type.ILLEGAL_ETYPE,
                "ETYPE is of the wrong type (consider replacing this with more informative message)");

    }

    public static SemanticError VariableNotFoundException(Token varNotFound ){
        return new SemanticError(Type.VARIABLE_NOT_FOUND,
                "ERROR, VARIABLE: " + varNotFound.getVal() + " of type: " + varNotFound.getType());
    }

}
