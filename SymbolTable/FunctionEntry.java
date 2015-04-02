package SymbolTable;

public class FunctionEntry extends SymbolTableEntry{
    int numberOfParameters;
    String parameterInfo;
    String result;

    public FunctionEntry(){
    }

    public FunctionEntry(String name, int numberOfParameters, String parameterInfo, String result){
        super(name);
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
        this.result = result;
    }

    @Override
    public void print () {
        System.out.println("Array Entry:");
        System.out.println(" Name                 : " + this.getName());
        System.out.println(" Number of parameters : " + this.getNumberOfParameters());
        System.out.println(" Parameter Info       : " + this.getParameterInfo());
        System.out.println(" Result               : " + this.getResult());
        System.out.println();
    }

    public int getNumberOfParameters(){
        return numberOfParameters;
    }

    public void setNumberOfParameters(int numberOfParameters){
        this.numberOfParameters = numberOfParameters;
    }

    public String getParameterInfo(){
        return parameterInfo;
    }

    public String getResult(){
        return result;
    }
}
