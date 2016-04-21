package SymbolTable;

import LexicalAnalyzer.*;
import GrammarSymbols.*;

public class VariableEntry extends SymbolTableEntry {

    int address;

    /***********************************************************
     These variables are used later

     boolean parm = false, functionResult = false, reserved = false;
     **************************************************************/

    public VariableEntry(String Name) {
        super(Name);
        this.setVariable(true);
    }

    public VariableEntry(String Name, TokenType type) {
        super(Name, type);
        this.setVariable(true);
    }

    public VariableEntry(String Name, TokenType type, int address){
        super(Name, type);
        this.address = address;
        this.setVariable(true);
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean isVariable() {
        return true;
    }

    public void print () {

        System.out.println("Variable Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Type    : " + this.getType());
        System.out.println("   Address : " + this.getAddress());
        System.out.println();
    }


}