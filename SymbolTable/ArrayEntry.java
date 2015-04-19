package SymbolTable;

import GrammarSymbols.TokenType;

public class ArrayEntry extends SymbolTableEntry{
    int address;
    int lowerBound;
    int upperBound;

    public ArrayEntry(String name){
        super(name);
    }
    public ArrayEntry(String name, TokenType type){
        super(name, type);
    }
    public ArrayEntry(String name, int address, TokenType type, int lowerBound, int upperBound){
        super(name, type);
        this.address = address;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
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
    public int getLowerBound(){
        return lowerBound;
    }
    public void setLowerBound(int newLowerBound){
        this.lowerBound = newLowerBound;
    }
    public int getUpperBound(){
        return upperBound;
    }
    public void setUpperBound(int newUpperBound){
        this.upperBound = newUpperBound;
    }
    public int getAddress(){
        return address;
    }
    public void setAddress(int newAddress){
        this.address = newAddress;
    }
}
