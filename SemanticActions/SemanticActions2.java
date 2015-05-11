package SemanticActions;

import java.lang.*;
import java.lang.reflect.Array;
import java.util.*;

import GrammarSymbols.*;
import CompilerError.*;
import SymbolTable.*;
import com.sun.tools.internal.ws.wsdl.document.jaxws.*;

public class SemanticActions2 {

    private Stack<Object> semanticStack;
    public Quadruple quads;
    private boolean insert;
    private boolean isArray;
    private boolean global;
    private int globalMemory;
    private int localMemory;
    private int varCounter;
    private int globalStore;
    private int localStore;
    private SymbolTableEntry currentFunction;
    private SymbolTableEntry nullOffset;
    public SymbolTable globalTable;
    private SymbolTable constantTable;
    public SymbolTable localTable;


    public SemanticActions2() {
        int tableSize = 53;
        semanticStack = new Stack<Object>();
        quads = new Quadruple();
        insert = false;
        isArray = false;

//        isParm = false;
        global = true;
        globalMemory = 0;
        localMemory = 0;
        varCounter = 1;
        nullOffset = new SymbolTableEntry("null", TokenType.INTCONSTANT);
        currentFunction = new SymbolTableEntry("null", TokenType.FUNCTION);
        globalTable = new SymbolTable(tableSize);
        constantTable = new SymbolTable(tableSize);
        localTable = new SymbolTable(tableSize);
    }

    //takes a mathematical symbol and converts it to an valid interpreter operand
    public String symToOp(Token symbol) {
        switch (symbol.getVal()) {
            case "*": {
                return "MUL";
            }
            case "/": {
                return "DIV";
            }
            case "+": {
                return "ADD";
            }
            case "-": {
                return "SUB";
            }
            default: {
                return "WARNING:" + symbol + "NOT A RECOGNIZED OPERAND TYPE";
            }
        }
    }


    //takes an id and replaces it with a memory address
    /*public String idToMemAddress(SymbolTableEntry id){
        if(global){
            return Integer.toString(id.getAddress());
        }
    }*/

    public VariableEntry create(String name, TokenType type) {
        VariableEntry newVar = new VariableEntry("$$" + name, type);
        if (global) {
            newVar.setAddress(globalMemory * -1);
            globalTable.insert(newVar);
            globalMemory += 1;
        } else {
            newVar.setAddress(localMemory * -1);
            localTable.insert(newVar);
            localMemory += 1;
        }
        return newVar;
    }

    //creates a temporary variable, inserts it into the symbol table with the correct name and counter
    public VariableEntry createTemp(TokenType type) {
        VariableEntry newVar = new VariableEntry("$$temp" + varCounter, type);
        varCounter += 1;
        if (global) {
            newVar.setAddress(globalMemory * -1);
            globalTable.insert(newVar);
            globalMemory += 1;
        } else {
            newVar.setAddress(localMemory * -1);
            localTable.insert(newVar);
            localMemory += 1;
        }
        return newVar;
    }

    public LinkedList<Integer> makeList(int[] quadTargets) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        for (int integer : quadTargets) {
            linkedList.add(integer);
        }
        return linkedList;
    }

    public LinkedList<Integer> merge(LinkedList<Integer> list1, LinkedList<Integer> list2) {
        LinkedList<Integer> mergedList = new LinkedList<>();
        mergedList.addAll(list1);
        mergedList.addAll(list2);
        return mergedList;
    }

    public void backPatch(LinkedList<Integer> quadIndices, int target) {
        for (int quadIndex : quadIndices) {
            //quad[4] = Integer.toString(target);
            String[] quad = quads.getQuad(quadIndex);
            quad[3] = Integer.toString(target);
        }

    }


    public int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2) {
        boolean id1IsReal = (id1.getType() == TokenType.REALCONSTANT || id1.getType() == TokenType.REAL);
        boolean id2IsReal = (id2.getType() == TokenType.REALCONSTANT || id2.getType() == TokenType.REAL);
        boolean id1IsInt = (id1.getType() == TokenType.INTCONSTANT || id1.getType() == TokenType.INTEGER);
        boolean id2IsInt = (id2.getType() == TokenType.INTCONSTANT || id2.getType() == TokenType.INTEGER);

        if (id1IsInt && id2IsInt) {
            return 0;
        } else if (id1IsReal && id2IsReal) {
            return 1;
        } else if (id1IsReal && id2IsInt) {
            return 2;
        } else if (id1IsInt && id2IsReal) {
            return 3;
        }
        //default return case indicates error?
        else {
            return -1;
        }
    }

    public void Execute(SemanticAction action, Token token) throws SemanticError {

        int actionNumber = action.getIndex();

        System.out.println("calling action : " + actionNumber + " with token " + token.getType() + ", " + token.getVal());

        switch (actionNumber) {

            case 1: {
                insert = true;
                break;

            }

            case 2: {
                insert = false;
                break;
            }

            case 3:{
                //case for an array variable
                Token typ = (Token)semanticStack.pop();
                if(isArray){
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
                            TokenType id_type = typ.getType();
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
                isArray = false;
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
                generate("CALL main","0");
                generate("EXIT");
                break;
            }

            case 13: {
                semanticStack.push(token);
                break;
            }

            case 30: {
                //assert token.getType() == TokenType.IDENTIFIER;
                if(global){
                    if (globalTable.lookup(token.getVal()) == null){
                        throw SemanticError.VariableNotFoundException(token);
                    }
                    else{
                        semanticStack.push(globalTable.lookup(token.getVal()));
                        semanticStack.push(EType.arithmetic);

                    }
                }
                else {
                    if (localTable.lookup(token.getVal()) == null) {
                        throw SemanticError.VariableNotFoundException(token);
                    }
                    else{
                        semanticStack.push(localTable.lookup(token.getVal()));
                        semanticStack.push(EType.arithmetic);

                    }
                }
                break;
            }

            case 31:{
                EType etype1 = (EType)semanticStack.pop();
                SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();
                //pop offset
                SymbolTableEntry offset = (SymbolTableEntry)semanticStack.pop();
                //IS THIS RIGHT?
                //EType etype2 = (EType)semanticStack.pop();
                SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
                if(typeCheck(id1, id2) == 3 || etype1 != EType.arithmetic){
                    throw SemanticError.GenericError();
                }
                if(typeCheck(id1, id2)==2){
                    SymbolTableEntry temp1 = createTemp(TokenType.REAL);
                    generate("LTOF", id1, temp1);
                    if(offset.getName().equals("null")){
                        generate("MOVE", temp1, id1);
                    }
                    else{
                        generate("STOR", temp1,offset,id1);
                    }
                }
                else if(offset.getName().equals("null")){
                    generate("MOVE", id2, id1);
                }
                else{
                    generate("MOVE",id2,offset,id1);
                }

                break;
            }

            case 34:{
                //if(((SymbolTableEntry)semanticStack.peek()).getType() == TokenType.FUNCTION){
                if(semanticStack.peek() instanceof FunctionEntry){
                    this.Execute(SemanticAction.action52,token);
                }
                else{
                    semanticStack.push(new ConstantEntry("null", TokenType.INTCONSTANT));
                }
                break;
            }

            case 40:{
                semanticStack.push(token);
                break;
            }

            case 42:{
                EType eType = (EType)semanticStack.pop();
                if (token.getType() == TokenType.RELOP && token.getVal().toLowerCase().equals("or")) {
                    /*if (eType != EType.relational){
                        throw SemanticError.IllegalETypeException();
                    }
                    LinkedList<Integer> EFalse = (LinkedList<Integer>)semanticStack.pop();
                    backPatch(EFalse, quads.getNextQuad());*/
                }
                else{
                    if(eType != EType.arithmetic){
                        throw SemanticError.IllegalETypeException();
                    }
                }
                semanticStack.push(token);
                break;
            }

            case 43:{
                EType eType = (EType)semanticStack.pop();
                if(eType == EType.relational) {

                }
                else{
                    SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();

                    if(eType != EType.arithmetic){
                        throw SemanticError.IllegalETypeException();
                    }
                    if (typeCheck(id1, id2) == 0 ){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.INTEGER);
                        generate(symToOp(operator), id1, id2, tempVar);
                        varCounter += 1;
                        semanticStack.push(tempVar);
                    }
                    else if (typeCheck(id1, id2) == 1 ){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.REAL);
                        generate("F"+symToOp(operator), id1, id2, tempVar);
                        varCounter += 1;
                        semanticStack.push(tempVar);
                    }
                    else if (typeCheck(id1, id2) == 2 ){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.REAL);
                        generate("LTOF", id1, id2, tempVar);
                        varCounter += 1;
                        VariableEntry tempVar2 = create("temp"+varCounter, TokenType.REAL);
                        generate("F"+symToOp(operator),id1,tempVar, tempVar2);
                        varCounter += 1;
                        semanticStack.push(tempVar2);
                    }
                    else if (typeCheck(id1, id2) == 3){
                        VariableEntry tempVar = create("temp"+varCounter, TokenType.REAL);
                        generate("LTOF", id1, tempVar);
                        VariableEntry tempVar2 = createTemp(TokenType.REAL);
                        varCounter += 1;
                        semanticStack.push(tempVar);
                        generate("F"+symToOp(operator), tempVar,id2, tempVar2);
                    }
                    semanticStack.push(EType.arithmetic);
                }
                break;
            }
            case 44:{
                EType eType = (EType)semanticStack.pop();
                if(eType == EType.relational){/*
                    if(token.getVal().equals("and")){
                        LinkedList<Integer> ETrue = (LinkedList<Integer>) semanticStack.pop();
                        backPatch(ETrue, quads.getNextQuad());
                    }*/
                }
                //push the operator
                semanticStack.push(token);
                break;
            }

            case 45:{
                if(token.getVal().toLowerCase().equals("and")){
                    /*LinkedList<Integer> E1True = (LinkedList<Integer>) semanticStack.pop();
                    LinkedList<Integer> E1False = (LinkedList<Integer>) semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    LinkedList<Integer> E2True = (LinkedList<Integer>) semanticStack.pop();
                    LinkedList<Integer> E2False = (LinkedList<Integer>) semanticStack.pop();*/
                    EType eType = (EType)semanticStack.pop();

                    if(eType != EType.relational){
                        throw SemanticError.IllegalETypeException();
                    }
                    else{
                        //LinkedList<Integer> ETrue = E2True;
                        //LinkedList<Integer> EFalse = merge(E1False, E2False);
                        //semanticStack.push(ETrue);
                        //semanticStack.push(EFalse);
                        semanticStack.push(EType.relational);
                    }
                }
                else{
                    EType eType = (EType)semanticStack.pop();
                    SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();

                    if(eType != EType.arithmetic){
                        throw SemanticError.IllegalETypeException();
                    }
                    if(typeCheck(id1, id2) != 0 && operator.getVal().equals("mod")){
                        throw SemanticError.ModOperandsException();//mismatch
                    }
                    if(typeCheck(id1, id2) == 0){
                        if(operator.getVal().toLowerCase().equals("mod")) {
                            SymbolTableEntry temp1 = createTemp(TokenType.INTEGER);
                            generate("MOVE", id1, temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.INTEGER);
                            generate("MOVE", id1, id2);
                            generate("SUB", temp2, id2, temp1);
                            //result will be in $$TEMP1
                            String resultLocation = Integer.toString(quads.getNextQuad() - 2);
                            generate("BGE", temp1, id2, resultLocation);
                            semanticStack.push(temp1);
                        }
                        else if(operator.getVal().equals("/")){
                            SymbolTableEntry temp1 = createTemp(TokenType.INTEGER);
                            generate("LTOF", id1, temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.REAL);
                            generate("LTOF", id2, temp2);
                            SymbolTableEntry temp3 = createTemp(TokenType.REAL);
                            generate("FDIV", temp1, temp2, temp3);
                            semanticStack.push(temp3);
                        }
                        else{
                            SymbolTableEntry temp = createTemp(TokenType.INTEGER);
                            generate(symToOp(operator),id1, id2, temp);
                            semanticStack.push(temp);
                        }
                    }
                    else if(typeCheck(id1, id2) == 1){
                        if (operator.getVal().toLowerCase().equals("div")){
                            SymbolTableEntry temp1 = createTemp(TokenType.INTEGER);
                            generate("FTOL",id1,temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.INTEGER);
                            generate("FTOL",id2,temp2);
                            SymbolTableEntry temp3 = createTemp(TokenType.INTEGER);
                            generate("DIV",temp1,temp2,temp3);
                            semanticStack.push(temp3);
                        }
                        else{
                            SymbolTableEntry temp1 = createTemp(TokenType.REAL);
                            generate("F"+symToOp(operator), id1, id2, temp1);
                            semanticStack.push(temp1);
                        }
                    }
                    else if(typeCheck(id1, id2) == 2){
                        if(operator.getVal().toLowerCase().equals("div")){
                            SymbolTableEntry temp1 = createTemp(TokenType.INTEGER);
                            generate("FTOL",id1,temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.INTEGER);
                            generate("DIV", temp1, id2, temp2);
                            semanticStack.push(temp2);
                        }
                        else{
                            SymbolTableEntry temp1 = createTemp(TokenType.REAL);
                            generate("LTOF",id2, temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.REAL);
                            generate("F"+symToOp(operator), id1, temp1, temp2);
                            semanticStack.push(temp2);
                        }
                    }
                    else if(typeCheck(id1,id2) == 3){
                        if(operator.getVal().toLowerCase().equals("div")){
                            SymbolTableEntry temp1 = createTemp(TokenType.INTEGER);
                            generate("FTOL", id2, temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.INTEGER);
                            generate("DIV", id1, temp1, temp2);
                            semanticStack.push(temp2);
                        }
                        else{
                            SymbolTableEntry temp1 = createTemp(TokenType.REAL);
                            generate("LTOF", id1, temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.REAL);
                            generate("F"+symToOp(operator),temp1, id2, temp2);
                            semanticStack.push(temp2);
                        }
                    }
                }
                semanticStack.push(EType.arithmetic);
                break;
            }

            case 46:{
                if(token.getType() == TokenType.IDENTIFIER){
                    if(global) {
                        if (globalTable.lookup(token.getVal()) == null){
                            throw SemanticError.VariableNotFoundException(token);
                        }
                        else{
                            semanticStack.push(globalTable.lookup(token.getVal()));
                        }
                    }
                    else{
                        if (localTable.lookup(token.getVal()) == null) {
                            throw SemanticError.VariableNotFoundException(token);
                        }
                        else{
                            semanticStack.push(localTable.lookup(token.getVal()));
                        }
                    }
                }
                else if(token.getType() == TokenType.INTCONSTANT || token.getType() == TokenType.REALCONSTANT){
                    if(constantTable.lookup(token.getVal()) == null){
                        if(token.getType() == TokenType.INTCONSTANT){
                            ConstantEntry constantEntry = new ConstantEntry(token.getVal(), TokenType.INTEGER);
                            semanticStack.push(constantEntry);
                            constantTable.insert(constantEntry);
                        }
                        else{
                            ConstantEntry constantEntry = new ConstantEntry(token.getVal(), TokenType.REAL);
                            semanticStack.push(constantEntry);
                            constantTable.insert(constantEntry);
                        }
                    }
                    else{
                        semanticStack.push(constantTable.lookup(token.getVal()));
                    }
                }
                semanticStack.push(EType.arithmetic);
                break;
            }

            /*case 48: {
                //NOTE: DO WE NOT POP THE OFFSET OFF IMMEDIATELY, INSTEAD OF ONLY IN THE ELSE CASE?
                //NOTE2: UNTIL THE NEXT PHASE, THE OFFSET IS ALWAYS NULL. THIS MUST BE CHANGED
                //currently assume that the offset is always null at this point
                SymbolTableEntry offset = (SymbolTableEntry)semanticStack.pop();
                if(!offset.getName().equals("null")){
                    SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                    //EType etype = (EType)semanticStack.pop();
                    if(offset.getType() != TokenType.INTEGER){
                        throw SemanticError.IllegalIndexException();
                    }
                    else{
                        VariableEntry temp1 = createTemp(id.getType());
                        generate("LOAD", id, offset, temp1);
                        semanticStack.push(temp1);
                        semanticStack.push(EType.arithmetic);
                    }
                }

                break;
            }*/

            case 55:{
                //backpatch
                generate("free", Integer.toString(globalMemory));
                generate("PROCEND");
                break;

            }

            case 56:{
                //generate("PROCBEGIN", globalTable.lookup("main"));
                generate("PROCBEGIN", "main");
                globalStore = quads.getNextQuad();
                generate("alloc");
                break;
            }

            default:{
                System.out.println("action not implemented (yet)");
                break;
            }

        }
    }




    private void generate (String tviCode){
        String[] newInstruction = {tviCode};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    private void generate (String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3) {
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("MOVE", operand1.getName(), tempVar);
            varCounter += 1;
        }
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("MOVE", operand2.getName(), tempVar);
            varCounter += 1;
        }
        if(operand3 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand3.getType());
            generate("MOVE", operand3.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, Integer.toString(operand1.getAddress()),
                Integer.toString(operand2.getAddress()), Integer.toString(operand3.getAddress())};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, String operand1, String operand2, SymbolTableEntry operand3){
        if(operand3 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand3.getType());
            generate("MOVE", operand3.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1, operand2, Integer.toString(operand3.getAddress())};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, String operand3){
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("MOVE",  operand1.getName(), tempVar);
            varCounter += 1;
        }
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("MOVE", operand2.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode,
                Integer.toString(operand1.getAddress()), Integer.toString(operand2.getAddress()), operand3};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode,SymbolTableEntry operand1, String operand2){
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("MOVE", operand1.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, Integer.toString(operand1.getAddress()), operand2};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode,SymbolTableEntry operand1, String operand2, SymbolTableEntry operand3){
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("MOVE", operand1.getName(), tempVar);
            varCounter += 1;
        }
        if(operand3 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand3.getType());
            generate("MOVE", operand3.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, Integer.toString(operand1.getAddress()), operand2, Integer.toString(operand3.getAddress())};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode,SymbolTableEntry operand1, SymbolTableEntry operand2){
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("MOVE", operand2.getName(), tempVar);
            varCounter += 1;
        }
        if(operand1 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand1.getType());
            generate("MOVE", tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode,
                Integer.toString(operand1.getAddress()), Integer.toString(operand1.getAddress())};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, String operand1, SymbolTableEntry operand2){
        if(operand2 instanceof ConstantEntry){
            SymbolTableEntry tempVar = create("temp"+varCounter, operand2.getType());
            generate("MOVE", operand2.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, operand1, Integer.toString(operand2.getAddress())};
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
            generate("MOVE", operand1.getName(), tempVar);
            varCounter += 1;
        }
        String[] newInstruction = {tviCode, Integer.toString(operand1.getAddress())};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();

    }
    public void printQuads(){
        this.quads.print();
    }


    /*private void gen(Object[] operators){
        String[] newInstruction = new String[4];
        assert operators.length == 4;
        //if any parameter is a constant entry, then a move instruction must be generated
        for(Object param : operators){
            if(param instanceof ConstantEntry){
                VariableEntry tempVar = createTemp(((ConstantEntry) param).getType());
                Object[] moveInstruction = {"MOVE", ((ConstantEntry) param).getName(), tempVar};
                gen(moveInstruction);
            }
        }
        //the tvi code is always the first parameter in the instruction
        newInstruction[0] = (String)operators[0];

        //case for when all three operators are symbol table entries
        if(operators[1] instanceof SymbolTableEntry &&
                operators[2] instanceof SymbolTableEntry &&
                operators[3] instanceof SymbolTableEntry){
            newInstruction[1] = Integer.toString(((SymbolTableEntry)operators[1]).getAddress());
            newInstruction[2] = Integer.toString(((SymbolTableEntry)operators[2]).getAddress());
            newInstruction[3] = Integer.toString(((SymbolTableEntry)operators[2]).getAddress());
        }
        //case for when the only operator is the tvi code
        else if(operators[1] == null && operators[2] == null & operators[3] == null){
        }
        //case for when the first two params are symbol table entries, and the third is a string
        else if(operators[1] instanceof SymbolTableEntry &&
                    operators[2] instanceof SymbolTableEntry &&
                    operators[3] instanceof String){
            newInstruction[1] = Integer.toString(((SymbolTableEntry)operators[1]).getAddress());
            newInstruction[2] = Integer.toString(((SymbolTableEntry)operators[2]).getAddress());
            newInstruction[3] = (String)operators[3];

        }
        //case for when the first param is a symbol table entry, and the second is a string
        else if(operators[1] instanceof SymbolTableEntry &&
                operators[2] instanceof String &&
                operators[3] == null){
            newInstruction[1] = Integer.toString(((SymbolTableEntry)operators[1]).getAddress());
            newInstruction[2] = (String)operators[2];
        }
        //case for when

        quads.addQuad(newInstruction);
        quads.incrementNextQuad();


    }*/
}