package SemanticActions;

import java.lang.*;
import java.util.*;

import GrammarSymbols.*;
import CompilerError.*;
import SymbolTable.*;

public class SemanticActions {

    private Stack<Object> semanticStack;
	private Quadruple quads ;
    private boolean insert;
    private boolean isArray;
    private boolean global;
    private int globalMemory;
    private int localMemory;
    private int varCounter;
    private int globalStore;
    private int localStore;
    public SymbolTable globalTable;
    private SymbolTable constantTable;
    public SymbolTable localTable;


    public SemanticActions() {
        int tableSize = 53;
        semanticStack = new Stack<Object>();
		quads = new Quadruple();
        insert = false;
        isArray = false;
//        isParm = false;
        global = true;
        globalMemory = 0;
        localMemory = 0;
        varCounter += 1;
        globalTable = new SymbolTable(tableSize);
        constantTable = new SymbolTable(tableSize);
        localTable = new SymbolTable(tableSize);
    }

    public VariableEntry create(String name, TokenType type){
        VariableEntry newVar = new VariableEntry("$$"+name, type);
        if(global){
            newVar.setAddress(globalMemory * -1);
            globalTable.insert(newVar);
        }
        else{
            newVar.setAddress(localMemory * -1);
            localTable.insert(newVar);
        }
        return newVar;
    }

    public int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2){

        if(id1.getType() == TokenType.INTCONSTANT && id2.getType() == TokenType.INTCONSTANT ||
                id1.getType() == TokenType.INTEGER && id2.getType() == TokenType.INTEGER){
            return 0;
        }
        else if(id1.getType() == TokenType.REALCONSTANT && id2.getType() == TokenType.REALCONSTANT){
            return 1;
        }

        else if(id1.getType() == TokenType.REALCONSTANT && id2.getType() == TokenType.INTCONSTANT){
            return 2;
        }

        else if(id1.getType() == TokenType.INTCONSTANT && id2.getType() == TokenType.REALCONSTANT){
            return 3;
        }
        //default return case indicates error?
        else{
            return -1;
        }
    }

    public void Execute(SemanticAction action, Token token) throws SemanticError {

        int actionNumber = action.getIndex();

        System.out.println("calling action : " + actionNumber + " with token " + token.getType() + ", " + token.getVal());

        switch (actionNumber) {
            //INSERT/SEARCH = INSERT
            case 1: {
                insert = true;
                break;

            }

            //INSERT/SEARCH = SEARCH
            case 2: {
                insert = false;
                break;
            }

            case 3:{
                //case for an array variable
                if(isArray){
                    Token typ = (Token)semanticStack.pop();
                    //lb and ub values are stored in tokens, and must be popped from the stack
                    Token ubToken = (Token)semanticStack.pop();
                    Token lbToken = (Token)semanticStack.pop();
                    //constants are stored as strings, and will be converted to floats for convenience
                    Float ub = Float.parseFloat(ubToken.getVal());
                    Float lb = Float.parseFloat(lbToken.getVal());
                    Float mSize = (ub - lb) + 1;
                    //the semantic stack must be confirmed to be not empty before checking the stack item type
                    while(!semanticStack.empty() && (semanticStack.peek() instanceof Token)){
                        //ensure that the next item on the semantic stack is an Identifier Token
                        if (((Token) semanticStack.peek()).getType() == TokenType.IDENTIFIER) {
                            Token id_token = (Token)semanticStack.pop();
                            String token_name = id_token.getVal();
                            TokenType token_type = typ.getType();
                            ArrayEntry newEntry = new ArrayEntry(token_name, token_type);
                            //case for if the variable gets stored in the global symbol table
                            if (global) {
                                newEntry.setAddress(globalMemory);
                                globalMemory += mSize;
                                globalTable.insert(newEntry);
                            //case for if the variable gets stored in the local symbol table
                            } else {
                                newEntry.setAddress(localMemory);
                                localMemory += mSize;
                                localTable.insert(newEntry);
                            }
                        }
                        //otherwise, the token is not an identifier token, and we exit the loop
                        else{
                            break;
                        }
                    }
                }
                //Case for simple variable
                else{
                    //the semantic stack must be confirmed to be not empty before checking the stack item type
                    while(!semanticStack.empty() && (semanticStack.peek() instanceof Token)){
                        //ensure that the next item on the semantic stack is an Identifier Token
                        if (((Token) semanticStack.peek()).getType() == TokenType.IDENTIFIER) {
                            Token id = (Token)semanticStack.pop();
                            String id_name = id.getVal();
                            TokenType id_type = id.getType();
                            VariableEntry newEntry = new VariableEntry(id_name, id_type);
                            //case for if the variable gets stored in the global symbol table
                            if (global) {
                                newEntry.setAddress(globalMemory);
                                globalTable.insert(newEntry);
                                globalMemory += 1;
                            //case for if the variable gets stored in the local symbol table
                            } else {
                                newEntry.setAddress(localMemory);
                                localTable.insert(newEntry);
                                localMemory += 1;
                            }
                        }
                        //otherwise, the token is not an identifier token, and we exit the loop
                        else{
                            break;
                        }
                    }

                }
                //break for the current case (case 3)
                break;
            }

            //push TYPE
            case 4: {
                semanticStack.push(token);
                break;
            }

            case 6: {
                isArray = true;
                break;
            }
            //push CONSTANT
            case 7: {
                semanticStack.push(token);
                //assert (token.getType() == TokenType.INTCONSTANT || token.getType() == TokenType.REALCONSTANT);
                break;
            }

            case 9:{
                while(!semanticStack.empty() && (semanticStack.peek() instanceof Token)) {
                    //ensure that the next item on the semantic stack is an Identifier Token
                    if (((Token) semanticStack.peek()).getType() == TokenType.IDENTIFIER) {
                        Token id = (Token)semanticStack.pop();
                        VariableEntry idEntry = new VariableEntry(id.getVal());
                        idEntry.setRestricted(true);
                        if(global){
                            globalTable.insert(idEntry);
                        }
                        else{
                            localTable.insert(idEntry);
                        }
                    }
                }
                insert = false;
                generate("CODE");
                generate("CALL",globalTable.lookup("main"),"0");
                generate("exit");
                break;
            }

            //push id
            case 13: {
                semanticStack.push(token);
                //assert (token.getType() == TokenType.IDENTIFIER);
                break;
            }

            case 30: {
                assert token.getType() == TokenType.IDENTIFIER;
                if(global){
                    if (globalTable.lookup(token.getVal()) == null){
                        throw SemanticError.VariableNotFoundException(token);
                    }
                }
                else {
                    if (globalTable.lookup(token.getVal()) == null) {
                        throw SemanticError.VariableNotFoundException(token);
                    }
                }
                semanticStack.push(token);
                break;
            }

            case 40:{
                semanticStack.push(token);
                break;
            }

            case 42:{
                EType eType = (EType)semanticStack.pop();
                if (token.getType() == TokenType.RELOP && token.getVal().toLowerCase().equals("or")) {
                    if (eType == EType.relational){
                        throw SemanticError.IllegalETypeException();
                    }
                }
                else{
                    if(eType == EType.arithmetic){
                        semanticStack.push(token);
                    }

                }
                semanticStack.push(token);
                break;
            }

            case 43:{
                SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();
                SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
                Token operator = (Token)semanticStack.pop();
                EType eType = (EType)semanticStack.pop();

                if(eType == EType.relational){
                }
                else{
                    if(eType != EType.arithmetic){
                        throw SemanticError.IllegalETypeException();
                    }
                    if (typeCheck(id1, id2) == 0 ){
                       VariableEntry tempVar = create("temp"+varCounter, TokenType.INTEGER);
                       generate(operator.getVal(), id1, id2, tempVar);
                        varCounter += 1;
                        semanticStack.push(tempVar);
                    }
                    else if (typeCheck(id1, id2) == 1 ){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.REAL);
                        generate(operator.getVal(), id1, id2, tempVar);
                        varCounter += 1;
                        semanticStack.push(tempVar);
                    }
                    else if (typeCheck(id1, id2) == 2 ){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.REAL);
                        generate("ltof", id1, id2, tempVar);
                        varCounter += 1;
                        VariableEntry tempVar2 = create("temp"+varCounter, TokenType.REAL);
                        generate(operator.getVal(),id1,tempVar, tempVar2);
                        varCounter += 1;
                        semanticStack.push(tempVar2);
                    }
                    else if (typeCheck(id1, id2) == 3){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.REAL);
                        generate("ltof", id1, tempVar);
                        varCounter += 1;
                        semanticStack.push(tempVar);
                    }
                    semanticStack.push(EType.arithmetic);
                }

            }

            case 56:{
                generate("PROCBEGIN", globalTable.lookup("main"));
                globalStore = quads.getNextQuad();
                //generate("alloc",)
            }

            default:{
                System.out.println("action not implemented (yet)");
                break;
            }


        }
    }
    /**
     * dumps the contents of the stack
     */
    public void dumpSemanticStack(){
        System.out.println("DUMPING SEMANTIC STACK!\n +" + "/------------------------------");
        int stackIndex = 0;
        while(!semanticStack.isEmpty()){
            System.out.println("[stack item "+ stackIndex+ "] " + semanticStack.pop());
            stackIndex += 1;
        }
    }
    private void generate (String tviCode){
        String[] newInstruction = {tviCode};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    private void generate (String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3) {
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("move"+operand2.getName(), tempVar);
            varCounter += 1;
        }
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("move"+operand2.getName(), tempVar);
            varCounter += 1;
        }
        if(operand3 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand3.getType());
            generate("move"+operand3.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1.getName(), operand2.getName(), operand3.getName()};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, String operand1, String operand2, SymbolTableEntry operand3){
        if(operand3 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand3.getType());
            generate("move"+operand3.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1, operand2, operand3.getName()};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    public void generate(String tviCode,SymbolTableEntry operand1, String operand2){
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("move"+operand1.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1.getName(), operand2};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    public void generate(String tviCode,SymbolTableEntry operand1, SymbolTableEntry operand2){
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("move"+operand2.getName(), tempVar);
            varCounter += 1;
        }
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("move"+operand1.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1.getName(), operand2.getName()};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    public void generate(String tviCode, String operand1, SymbolTableEntry operand2){
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("move"+operand2.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1, operand2.getName()};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    public void generate(String tviCode, String operand1){
        String[] newInstruction = {tviCode, operand1};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();

    }
    public void generate(String tviCode, SymbolTableEntry operand1){
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("move"+operand1.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1.getName()};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();

    }
    public void printQuads(){
        this.quads.print();
    }
}
