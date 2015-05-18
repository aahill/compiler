package SymbolTable;

import GrammarSymbols.*;

import java.util.LinkedList;

public class FunctionEntry extends SymbolTableEntry{
    int numberOfParameters;
    private LinkedList<ParamEntry> parameterInfo;
    SymbolTableEntry result;

    public FunctionEntry(String name){
        super(name);
        this.setFunction(true);
        parameterInfo = new LinkedList<>();
    }
    public FunctionEntry(String name, int numberOfParameters, SymbolTableEntry result){
        super(name);
        this.numberOfParameters = numberOfParameters;
        this.result = result;
        this.setFunction(true);
        parameterInfo = new LinkedList<>();
    }

    @Override
    public void print () {
        System.out.println("Function Entry:");
        System.out.println(" Name                 : " + this.getName());
        System.out.println(" Number of parameters : " + this.getNumberOfParameters());
        System.out.println(" Result               : " + this.getResult());
        System.out.println();
    }

    //returns the number of parameters in the function entry
    public int getNumberOfParameters(){
        return parameterInfo.size();
    }

    //sets the number of parameters
    public void setNumberOfParameters(int numberOfParameters){
        this.numberOfParameters = numberOfParameters;
    }

    //adds an element to the parameter info
    public void addParameterInfo(ParamEntry param){
        this.parameterInfo.add(param);
    }
    public void setResult(SymbolTableEntry newResult){
        this.result = newResult;
    }

    //returns the result of the function
    public SymbolTableEntry getResult(){
        return result;
    }

    public ParamEntry getParameters(int index){
        return this.parameterInfo.get(index);
    }
    @Override
    public boolean isFunction(){
        return true;
    }
}
