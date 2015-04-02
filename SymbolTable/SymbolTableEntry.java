package SymbolTable;

import GrammarSymbols.*;

public class SymbolTableEntry {
    private String name;
    private TokenType type;

    //basic constructor
    public SymbolTableEntry(){
    }

    //basic constructor
    public SymbolTableEntry(String name){
        this.name = name;
    }

    //constructor for when identifier corresponds to a constant type
    public SymbolTableEntry(String name, TokenType type){
        this.name = name;
        this.type = type;
    }

    public String getName(){
        return this.name;
    }

    public TokenType getType(){
        return this.type;
    }

    //default print statement to be overloaded by subclass
    public void print(){
        System.out.println("<IDENTIFIER : IDENTIFIER_VALUE>");
    }

}
