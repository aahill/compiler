package SymbolTable;

import GrammarSymbols.TokenType;

public class ArrayEntry extends SymbolTableEntry{
    int address;
    ConstantEntry lowerBound;
    ConstantEntry upperBound;

    public ArrayEntry(String name){
        super(name);
        this.setArray(true);
    }
    public ArrayEntry(String name, TokenType type){
        super(name, type);
        this.setArray(true);
    }
    public ArrayEntry(String name, int address, TokenType type, ConstantEntry lowerBound, ConstantEntry upperBound){
        super(name, type);
        this.address = address;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.setArray(true);
    }
    //overriding base-class print method
    @Override
    public void print () {

        System.out.println("Array Entry:");
        System.out.println("   Name      : " + this.getName());
        System.out.println("   Address   :" + this.getAddress());
        System.out.println("   Type      : " + this.getType());
        System.out.println(" Upper bound : " + this.getUpperBound());
        System.out.println(" Lower bound : " + this.getLowerBound());
        System.out.println();
    }
    public ConstantEntry getLowerBound(){
        return lowerBound;
    }
    public void setLowerBound(ConstantEntry newLowerBound){
        this.lowerBound = newLowerBound;
    }
    public ConstantEntry getUpperBound(){
        return upperBound;
    }
    public void setUpperBound(ConstantEntry newUpperBound){
        this.upperBound = newUpperBound;
    }
    public int getAddress(){
        return address;
    }
    public void setAddress(int newAddress){
        this.address = newAddress;
    }
    @Override
    public boolean isArray(){
        return true;
    }

}
