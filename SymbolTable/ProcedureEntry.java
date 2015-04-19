package SymbolTable;

public class ProcedureEntry extends SymbolTableEntry {

    private int numberOfParameters;
    private String parameterInfo;

    public ProcedureEntry(String name){
        super(name);
    }

    public ProcedureEntry(String name, int numberOfParameters){
        super(name);
        this.numberOfParameters = numberOfParameters;
    }
    public ProcedureEntry(String name, int numberOfParameters, String parameterInfo){
        super(name);
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
    }

    @Override
    public void print () {
        System.out.println("Parameter Entry:");
        System.out.println(" Name                 : " + this.getName());
        System.out.println(" Number of parameters : " + this.getNumberOfParameters());
        System.out.println(" Parameter Info       : " + this.getParameterInfo());
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

    //returns the parameter info
    public void setParameterInfo(String newParameterInfo){
        this.parameterInfo = newParameterInfo;
    }

}
