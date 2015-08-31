package SymbolTable;

import GrammarSymbols.*;

import java.util.LinkedList;

public class SymbolTableEntry {
    private String name;
    private TokenType type;
    private Boolean restricted;
    private int address;
    private boolean isConstant;
    private boolean isFunction;
    private boolean isProc;
    private boolean isArray;
    private boolean isVariable;
    private boolean isParameter;
    private boolean isResult;

    //basic constructor
    public SymbolTableEntry(String name) {
        this.name = name;
        this.restricted = false;
    }

    //constructor for when identifier corresponds to a constant type
    public SymbolTableEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
        // entries are not restricted by default
        this.restricted = false;
        this.isConstant = false;
        this.isProc = false;
        this.isFunction = false;
        this.isArray = false;
        this.isVariable = false;
        this.isParameter = false;
        this.isResult = false;

    }

    public String getName() {
        return this.name;
    }
    public String toString(){
        return this.getName() + " " + this.getType();
    }


    public TokenType getType() {
        return this.type;
    }

    public void setType(TokenType newType) {
        this.type = newType;
    }
    public int getAddress() {
        return this.address;
    }

    public void setRestricted(Boolean isRestricted) {
        this.restricted = isRestricted;
    }

    //default print statement to be overloaded by subclass
    public void print() {
        System.out.println("Generic Entry:");
        System.out.println(" Name : " + this.getName());
        System.out.println(" Type : " + this.getType());
    }

    public boolean isConstant() {
        return this.isConstant;
    }
    public void setConstant(boolean isConst){
        this.isConstant = isConst;
    }

    public boolean isFunction() {
        return this.isFunction;
    }
    public void setFunction(boolean funcBool) {
        this.isFunction = funcBool;
    }

    public boolean isArray() {
        return this.isArray;
    }
    public void setArray(boolean setArray){
        this.isArray = setArray;
    }


    public boolean isParam() {
        return this.isParameter;
    }
    public void setParamStatus(boolean param) {
        this.isParameter = param;
    }

    public boolean isProc() {
        return this.isProc;
    }
    public void setProc(boolean setProc){
        this.isProc = setProc;
    }

    public boolean isResult() {
        return this.isResult;
    }
    public void setAsResult(boolean setResult){
        this.isResult = setResult;
    }
    public void setAddress(int newAddress){
        this.address = newAddress;
    }

    public boolean isVariable(){
        return this.isVariable;
    }
    public void setVariable(boolean isVar){
        this.isVariable = isVar;
    }



}
