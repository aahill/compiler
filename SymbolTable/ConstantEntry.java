package SymbolTable;

import GrammarSymbols.TokenType;

public class ConstantEntry extends SymbolTableEntry{
    public ConstantEntry(String name, TokenType type) {
        super(name, type);
    }

    public void print() {

        System.out.println("Variable Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Type    : " + this.getType());
        System.out.println();
    }

}
