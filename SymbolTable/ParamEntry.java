package SymbolTable;

import GrammarSymbols.*;


public class ParamEntry extends SymbolTableEntry {
    private ConstantEntry upperBound;
    private ConstantEntry lowerBound;
    private boolean isArray;


    public ParamEntry(String name){
        super(name);
        this.setParamStatus(true);
        this.isArray = false;
        this.setParamStatus(true);
    }

    public ParamEntry(String name, TokenType type){
        super(name,type);
        this.setParamStatus(true);
        this.isArray = false;
        this.setParamStatus(true);
    }

    public void setUpperBound(ConstantEntry newUB){
        this.upperBound = newUB;
    }
    public void setLowerBound(ConstantEntry newLB){
        this.lowerBound = newLB;
    }
    public void setIsArray(boolean arrayBool){
        isArray = arrayBool;
    }

    public boolean isArray() {
        return this.isArray;
    }

    public ConstantEntry getUpperBound(){
        return this.upperBound;
    }
    public ConstantEntry getLowerBound(){
        return this.lowerBound;
    }
}
