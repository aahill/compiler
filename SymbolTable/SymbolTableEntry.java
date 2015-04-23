package SymbolTable;

import GrammarSymbols.*;

public class SymbolTableEntry {
    private String name;
    private TokenType type;
    private Boolean restricted;

    //basic constructor
    public SymbolTableEntry(String name){
        this.name = name;
    }

    //constructor for when identifier corresponds to a constant type
    public SymbolTableEntry(String name, TokenType type){
        this.name = name;
        this.type = type;
        // entries are not restricted by default
        this.restricted = false;
    }

    public String getName(){
        return this.name;
    }

    public TokenType getType(){
        return this.type;
    }

    public void setRestricted(Boolean isRestricted){
        this.restricted = isRestricted;
    }

    //default print statement to be overloaded by subclass
    public void print(){
        System.out.println("Generic Entry:");
        System.out.println(" Name : " + this.getName());
        System.out.println(" Type : " + this.getType());
    }

}
