package SymbolTable;

import java.util.LinkedList;

public class ProcedureEntry extends SymbolTableEntry {

    private int numberOfParameters;
    private LinkedList<ParamEntry> parameterInfo;

    public ProcedureEntry(String name){
        super(name);
        this.setFunction(true);
        parameterInfo = new LinkedList<>();
        this.setProc(true);
    }

    public ProcedureEntry(String name, int numberOfParameters){
        super(name);
        this.numberOfParameters = numberOfParameters;
        parameterInfo = new LinkedList<>();
        this.setProc(true);
    }
    public ProcedureEntry(String name, int numberOfParameters, LinkedList<SymbolTableEntry> parameterInfo){
        super(name);
        this.numberOfParameters = numberOfParameters;
        parameterInfo = new LinkedList<SymbolTableEntry>();
        this.setProc(true);
    }

    @Override
    public void print () {
        System.out.println("Parameter Entry:");
        System.out.println(" Name                 : " + this.getName());
        System.out.println(" Number of parameters : " + this.getNumberOfParameters());
        //System.out.println(" Parameter Info       : " + this.getParameterInfo());
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

    //adds an element to the parameter info
    public void addParameterInfo(ParamEntry param){
        this.parameterInfo.add(param);
    }
    public ParamEntry getParameters(int index){
        return this.parameterInfo.get(index);
    }

    @Override
    public boolean isProc(){
        return true;
    }

}
