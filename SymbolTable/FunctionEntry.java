package SymbolTable;

public class FunctionEntry extends SymbolTableEntry{
    int numberOfParameters;
    String parameterInfo;
    String result;

    public FunctionEntry(String name, int numberOfParameters, String parameterInfo, String result){
        super(name);
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
        this.result = result;
    }

    @Override
    public void print () {
        System.out.println("Function Entry:");
        System.out.println(" Name                 : " + this.getName());
        System.out.println(" Number of parameters : " + this.getNumberOfParameters());
        System.out.println(" Parameter Info       : " + this.getParameterInfo());
        System.out.println(" Result               : " + this.getResult());
        System.out.println();
    }

    //returns the number of parameters in the function entry
    public int getNumberOfParameters(){
        return numberOfParameters;
    }

    //sets the number of parameters
    public void setNumberOfParameters(int numberOfParameters){
        this.numberOfParameters = numberOfParameters;
    }

    //returns the parameter info
    public String getParameterInfo(){
        return parameterInfo;
    }

    //returns the result of the function
    public String getResult(){
        return result;
    }
}
