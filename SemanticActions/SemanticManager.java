package SemanticActions;

import java.lang.*;
import java.util.*;

import GrammarSymbols.*;
import CompilerError.*;
import SymbolTable.*;

public class SemanticManager {

    private Stack<Object> semanticStack;
    //	Quadruples not used until Phase 2
//	private Quadruples quads ;
    private boolean insert;
    private boolean isArray;
    private boolean global;
    private int globalMemory;
    private int localMemory;
    public SymbolTable globalTable;
    private SymbolTable constantTable;
    public SymbolTable localTable;


    public SemanticManager(int tableSize) {
        semanticStack = new Stack<Object>();
//		quads = new Quadruples();
        insert = false;
        isArray = false;
//        isParm = false;
        global = true;
        globalMemory = 0;
        localMemory = 0;
        globalTable = new SymbolTable(tableSize);
//        constantTable = new SymbolTable(tableSize);
        localTable = new SymbolTable(tableSize);
//        InstallBuiltins(globalTable);
    }

    public void Execute(SemanticAction action, Token token) throws SemanticError {

        int actionNumber = action.getIndex();

        System.out.println("calling action : " + actionNumber + " with token " + token.getType());

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
                if(isArray){
                    //constants are stored as strings, and will be converted to floats for convenience
                    Float ub = Float.parseFloat((String)semanticStack.pop());
                    Float lb = Float.parseFloat((String)semanticStack.pop());
                    Float mSize = (ub - lb) + 1;
                    //the semantic stack must be confirmed to be not empty before checking the stack item type
                    while(!semanticStack.empty() && (semanticStack.peek() instanceof Token)){
                        if (((Token) semanticStack.peek()).getType() == TokenType.IDENTIFIER) {
                            Token id_token = (Token)semanticStack.pop();
                            String token_name = id_token.getVal();
                            TokenType token_type = id_token.getType();
                            ArrayEntry newEntry = new ArrayEntry(token_name, token_type);
                            //case for if the variable
                            if (global) {
                                newEntry.setAddress(globalMemory);
                                globalMemory += mSize;
                                globalTable.insert(newEntry);
                            } else {
                                newEntry.setAddress(localMemory);
                                localMemory += mSize;
                                localTable.insert(newEntry);
                            }
                        }
                        else{
                            break;
                        }
                    }
                }
                //Case for simple variable
                else{
                    while(!semanticStack.empty() && (semanticStack.peek() instanceof Token)){
                        if (((Token) semanticStack.peek()).getType() == TokenType.IDENTIFIER) {
                            Token id = (Token)semanticStack.pop();
                            String id_name = id.getVal();
                            TokenType id_type = id.getType();
                            VariableEntry newEntry = new VariableEntry(id_name, id_type);
                            if (global) {
                                newEntry.setAddress(globalMemory);
                                globalTable.insert(newEntry);
                                globalMemory += 1;
                            } else {
                                newEntry.setAddress(localMemory);
                                localTable.insert(newEntry);
                                localMemory += 1;
                            }
                        }
                        else{
                            break;
                        }
                    }

                }
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
            }

            //push id
            case 13: {
                semanticStack.push(token);
                //assert (token.getType() == TokenType.IDENTIFIER);
            }


        }
    }
    /**
     * dumps the contents of the stack
     */
    public void dumpStack(){
        System.out.println("DUMPING SEMANTIC STACK!\n +" + "/------------------------------");
        int stackIndex = 0;
        while(!semanticStack.isEmpty()){
            System.out.println("[stack item "+ stackIndex+ "] " + semanticStack.pop());
            stackIndex += 1;
        }
    }
}
