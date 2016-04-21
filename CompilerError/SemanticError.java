package CompilerError;
import GrammarSymbols.Token;
import SemanticActions.EType;
import SymbolTable.*;

import java.util.function.Function;

/** Exception class thrown when an error is encountered during semantic construction. */
public class SemanticError extends CompilerError {

    public SemanticError( Type errorNumber, String message){
        super(errorNumber, message);
    }

    public static SemanticError IllegalETypeException(EType expectedEtype, EType receivedEtype, int lineNumber, String line){
        return new SemanticError(Type.ILLEGAL_ETYPE,
                ">>>ERROR: Expected " + expectedEtype.toString() + "statement. Instead received" + receivedEtype.toString()+".\n"+
                        "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError IllegalTypeConversion(String illegalType, int lineNumber, String line){
        return new SemanticError(Type.ILLEGAL_TYPE_CONVERSION,
                ">>>ERROR: Illegal type coercion (from"+ illegalType + " to real number) detected.\n" +
                        "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError ProcedureParameterException( int lineNumber, String line){
        return new SemanticError(Type.PROCEDURE_PARAMETER_ERROR,
                ">>>ERROR: malformed procedure/function parameters detected.\n" +
                        "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError IllegalParameterTypeException(int lineNumber, String line){
        return new SemanticError(Type.ILLEGAL_PARAMETER,
                ">>>ERROR: illegal parameter type detected.\n" +
                        "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError NotArrayException(SymbolTableEntry incorrect, int lineNumber, String line){
        return new SemanticError(Type.NOT_ARRAY,
                ">>>ERROR: Expected an array, instead recieved " + incorrect.getType() + ".\n"+
                "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError ExpectedParamNumberExceededException(SymbolTableEntry exceeded, int lineNumber, String line){
        int expectedParams;
        if(exceeded instanceof ProcedureEntry){
            expectedParams = ((ProcedureEntry) exceeded).getNumberOfParameters();
        }
        else{
            expectedParams = ((FunctionEntry) exceeded).getNumberOfParameters();
        }
        return new SemanticError(Type.TOO_MANY_PARAMETERS,
                ">>>ERROR: number of expected parameters for " + exceeded.getName() + "encountered.\n" +
                        "Expected only"+ expectedParams + "parameters.\n"+
                        "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError ParameterMismatchException(
            SymbolTableEntry misMatched, SymbolTableEntry expected, SymbolTableEntry received, int lineNumber, String line){
        return new SemanticError(Type.PARAMETER_MISMATCH,
                ">>>ERROR: encountered different parameter type than expected for function/procedure "+ misMatched.getName() + ".\n" +
                "recieved parameter of type " + received.getType().toString() + "instead of " + expected.getType().toString() + ".\n" +
                "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError ArrayParameterMismatchException(
            SymbolTableEntry misMatched, ParamEntry expected, ArrayEntry received, int lineNumber, String line){
        return new SemanticError(Type.PARAMETER_MISMATCH,
                ">>>ERROR: encountered different array bounds than expected for function/procedure " + misMatched.getName() + ".\n" +
                "recieved array parameter from " + received.getLowerBound() + "to " + received.getUpperBound() + ".\n" +
                "Expected array parameter from " + expected.getLowerBound() + "to " + expected.getUpperBound() + ".\n" +
                "Found on line " + lineNumber + ": " + line);
    }
    public static SemanticError NotProcOrFunctException(SymbolTableEntry incorrect, int lineNumber, String line){
        return new SemanticError(Type.NOT_PROC_OR_FUNCT,
                ">>>ERROR: procedure/function expected. Instead found non procedure/function " + incorrect.getName() + ".\n" +
                "Found on line " + lineNumber + ": " + line);
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
    public static SemanticError CurrentFunctionMismatchException(String expectedFunct, String recievedFunct, int lineNum, String line){
        return new SemanticError(Type.CURRENT_FUNCTION_MISMATCH,
                ">>>ERROR: The function " + recievedFunct + "Does not match " + expectedFunct+ ", the function currently being processed.\n"+
                "Found on line " + lineNum + ": " + line);
    }
    //public static SemanticError Illegal
    public static SemanticError GenericError(){
        return new SemanticError(Type.TEST,
                "GENERIC ERROR, MUST REPLACE");
    }

}
