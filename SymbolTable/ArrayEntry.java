package SymbolTable;

import GrammarSymbols.TokenType;

public class ArrayEntry extends SymbolTableEntry{
    int address;
    int lowerBound;
    int upperBound;

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
    public int getUpperBound(){
        return upperBound;
    }
    public int getAddress(){
        return address;
    }
}
