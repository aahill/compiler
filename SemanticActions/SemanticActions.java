package SemanticActions;
//57 not 46
//57 has to do with constant
//57 and 58 insert constant entries in the constant table
//they do not pop, but create new entries (shouldn't cause problems)

import java.lang.*;
import java.util.*;

import GrammarSymbols.*;
import CompilerError.*;
import SymbolTable.*;

public class SemanticActions {

    private Stack<Object> semanticStack;
    public Quadruple quads ;
    private boolean insert;
    private boolean isArray;
    private boolean global;
    private int globalMemory;
    private int localMemory;
    private int varCounter;
    private int globalStore;
    private int localStore;
    private int tableSize;
    private Stack<Integer> parmCountStack;
    private ParamEntry nextParm;
    private Stack<LinkedList<ParamEntry>> nextParmStack;
    private Stack<Integer>parmCountStackIndex;
    private String currentFunction;
    public SymbolTable globalTable;
    private SymbolTable constantTable;
    public SymbolTable localTable;


    public SemanticActions() {
        tableSize = 53;
        parmCountStack = new Stack<Integer>();
        parmCountStackIndex = new Stack<>();
        semanticStack = new Stack<Object>();
        nextParmStack = new Stack<>();
        quads = new Quadruple();
        insert = false;
        isArray = false;
//        isParm = false;
        global = true;
        globalMemory = 0;
        localMemory = 0;
        varCounter = 1;
        currentFunction = null;
        globalTable = new SymbolTable(tableSize);
        constantTable = new SymbolTable(tableSize);
        localTable = new SymbolTable(tableSize);
    }
    //takes a mathematical symbol and converts it to an valid interpreter operand
    public String symToOp(Token symbol){
        switch (symbol.getVal()){
            case "*":{
                return "MUL";
            }
            case "/":{
                return "DIV";
            }
            case "+":{
                return "ADD";
            }
            case "-":{
                return "SUB";
            }
            default:{
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
    public void dumpStack(){
        System.out.println("----------------------------------------------------------");
        for(Object item : semanticStack){
            System.out.println(item.toString());
        }
        System.out.println("----------------------------------------------------------");
    }

    public VariableEntry create(String name, TokenType type){
        VariableEntry newVar = new VariableEntry("$$"+name, type);
        newVar.setAsResult(true);
        if(global){
            newVar.setAddress(globalMemory * -1);
            globalTable.insert(newVar);
            globalMemory += 1;
        }
        else{
            newVar.setAddress(localMemory * -1);
            localTable.insert(newVar);
            localMemory += 1;
        }
        return newVar;
    }
    //creates a temporary variable, inserts it into the symbol table with the correct name and counter
    public VariableEntry createTemp(TokenType type){
        VariableEntry newVar = new VariableEntry("$$temp"+varCounter, type);
        varCounter += 1;
        if(global){
            newVar.setAddress(globalMemory * -1);
            globalTable.insert(newVar);
            globalMemory += 1;
        }
        else{
            newVar.setAddress(localMemory * -1);
            localTable.insert(newVar);
            localMemory += 1;
        }
        return newVar;
    }
    public LinkedList<Integer> makeList(int quadTarget) {
        LinkedList <Integer>linkedList = new LinkedList<>();
        linkedList.add(quadTarget);
        return linkedList;

    }
    public LinkedList<Integer> merge(LinkedList<Integer> list1, LinkedList<Integer> list2){
        LinkedList <Integer>mergedList = new LinkedList<>();
        mergedList.addAll(list1);
        mergedList.addAll(list2);
        return mergedList;
    }
    public void backPatch(LinkedList<Integer> quadIndices, int target){
        for(int quadIndex : quadIndices){
            String[] quad = quads.getQuad(quadIndex);
            quad[3] = Integer.toString(target);
        }

    }


    public int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2){
        boolean id1IsReal = (id1.getType() == TokenType.REALCONSTANT || id1.getType() == TokenType.REAL);
        boolean id2IsReal = (id2.getType() == TokenType.REALCONSTANT || id2.getType() == TokenType.REAL);
        boolean id1IsInt = (id1.getType() == TokenType.INTCONSTANT || id1.getType() == TokenType.INTEGER);
        boolean id2IsInt = (id2.getType() == TokenType.INTCONSTANT || id2.getType() == TokenType.INTEGER);

        if(id1IsInt && id2IsInt){
            return 0;
        }
        else if(id1IsReal && id2IsReal){
            return 1;
        }

        else if(id1IsReal && id2IsInt){
            return 2;
        }

        else if(id1IsInt && id2IsReal){
            return 3;
        }
        //default return case indicates error?
        else{
            return -1;
        }
    }

    public void Execute(SemanticAction action, Token token, int lineNumber, String line) throws SemanticError {

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
                            newEntry.setLowerBound(new ConstantEntry(lbToken.getVal(),TokenType.INTEGER));
                            newEntry.setUpperBound(new ConstantEntry(ubToken.getVal(),TokenType.INTEGER));
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

            case 5:{
                insert = false;
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                generate("PROCBEGIN", id);
                localStore = quads.getNextQuad();
                generate("alloc");
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
                    else{
                        break;
                    }
                }
                insert = false;
                generate("CODE");
                //generate("CALL",globalTable.lookup("main"),"0");
                generate("CALL main","0");
                generate("EXIT");
                break;
            }

            case 11:{
                global = true;
                //delete local symbol table; let garbage handling deallocate it
                localTable = null;
                currentFunction = null;
                quads.setField(localStore,3,Integer.toString(localMemory));
                generate("FREE", Integer.toString(localMemory));
                generate("PROCEND");
                break;
            }

            //push id
            case 13: {
                semanticStack.push(token);
                break;
            }

            case 15: {
                FunctionEntry functEntry = new FunctionEntry(token.getVal());
                globalTable.insert(functEntry);
                SymbolTableEntry funName = create(functEntry.getName(), TokenType.INTEGER);
                functEntry.setResult(funName);
                semanticStack.push(functEntry);
                global = false;
                localTable = new SymbolTable(this.tableSize);
                localMemory = 0;
                break;
            }

            case 16:{
                TokenType type = ((Token) semanticStack.pop()).getType();
                SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
                id.setType(type);
                SymbolTableEntry functionName = lookup("$$"+id.getName());
                functionName.setType(type);
                currentFunction = id.getName();
                break;
            }

            case 17:{
                ProcedureEntry procEntry = new ProcedureEntry(token.getVal());
                globalTable.insert(procEntry);
                semanticStack.push(procEntry);
                global = false;
                localTable = new SymbolTable(this.tableSize);
                localMemory = 0;
                break;
            }

            case 19:{
                parmCountStack.push(0);
                break;
            }

            case 20:{
                int parmCount = parmCountStack.pop();
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
                if(id instanceof ProcedureEntry) {
                    ((ProcedureEntry)id).setNumberOfParameters(parmCount);
                }
                else{
                    ((FunctionEntry)id).setNumberOfParameters(parmCount);
                }
                break;
            }

            case 21: {
                TokenType type = ((Token) semanticStack.pop()).getType();
                SymbolTableEntry id = null;
                for(Object stackItem : semanticStack){
                    if (stackItem instanceof FunctionEntry || stackItem instanceof ProcedureEntry){
                        id = (SymbolTableEntry) stackItem;
                    }
                }
                while (!semanticStack.empty() && semanticStack.peek() instanceof Token) {
                    if (isArray) {
                        //create symbol table entry
                        ConstantEntry const1 = new ConstantEntry(((Token)semanticStack.pop()).getVal(),type);
                        ConstantEntry const2 = new ConstantEntry(((Token)semanticStack.pop()).getVal(),type);
                        Token parmToken = (Token) semanticStack.pop();
                        ArrayEntry arrayParm = new ArrayEntry(parmToken.getVal(),type);
                        if (global) {
                            globalTable.insert(arrayParm);
                            arrayParm.setAddress(globalMemory);
                            globalMemory += 1;
                        } else {
                            localTable.insert(arrayParm);
                            arrayParm.setAddress(localMemory);
                            localMemory += 1;
                        }
                        globalTable.insert(arrayParm);
                        arrayParm.setUpperBound(const1);
                        arrayParm.setLowerBound(const2);

                        //create parminfo section
                        ParamEntry paramEntry = new ParamEntry(parmToken.getVal(), type);
                        paramEntry.setIsArray(true);
                        paramEntry.setUpperBound(const1);
                        paramEntry.setLowerBound(const2);
                        if(id instanceof ProcedureEntry) {
                            ((ProcedureEntry) id).addParameterInfo(paramEntry);
                        }
                        else{
                            ((FunctionEntry) id).addParameterInfo(paramEntry);
                        }

                    } else {
                        Token parmToken = (Token) semanticStack.pop();
                        VariableEntry parmEntry = new VariableEntry(parmToken.getVal(), type);
                        parmEntry.setParamStatus(true);
                        if (global) {
                            globalTable.insert(parmEntry);
                            parmEntry.setAddress(globalMemory);
                            globalMemory += 1;
                        } else {
                            localTable.insert(parmEntry);
                            parmEntry.setAddress(localMemory);
                            localMemory += 1;
                        }
                        //create parmInfo entry
                        ParamEntry parm = new ParamEntry(parmToken.getVal(), type);
                        parm.setIsArray(false);
                        if(id instanceof FunctionEntry) {
                            ((FunctionEntry) id).addParameterInfo(parm);
                        }
                        else{
                            ((ProcedureEntry)id).addParameterInfo(parm);
                        }
                    }
                    //id.setAddress(localMemory);
                    //localMemory += 1;
                    id.setType(type);
                    int counter = parmCountStack.pop();
                    counter += 1;
                    parmCountStack.push(counter);
                    //nextParm.setType(type);
                }
                isArray = false;
                break;
            }

            case 22:{
                EType eType = (EType) semanticStack.pop();
                if(eType != EType.relational){
                    throw SemanticError.IllegalETypeException();
                }
                else{
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> EFalse = (LinkedList<Integer>) semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> ETrue = (LinkedList<Integer>) semanticStack.peek();
                    semanticStack.push(EFalse);
                    backPatch(ETrue, quads.getNextQuad());
                }
                break;
            }

            case 24:{
                int beginLoop = quads.getNextQuad();
                semanticStack.push(beginLoop);
                break;
            }

            case 25:{
                EType eType = (EType)semanticStack.pop();
                if(eType != EType.relational){
                    throw SemanticError.IllegalETypeException();
                }
                else{
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> EFalse = (LinkedList<Integer>)semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> ETrue = (LinkedList<Integer>)semanticStack.peek();
                    semanticStack.push(EFalse);
                    backPatch(ETrue, quads.getNextQuad());
                }
                break;
            }

            case 26:{
                @SuppressWarnings("unchecked")
                LinkedList<Integer> EFalse = (LinkedList<Integer>)semanticStack.pop();
                @SuppressWarnings("unchecked")
                LinkedList<Integer> ETrue = (LinkedList<Integer>)semanticStack.pop();
                int beginLoop = (int)semanticStack.pop();
                generate("GOTO", Integer.toString(beginLoop));
                backPatch(EFalse,quads.getNextQuad());
                break;
            }

            case 27:{
                LinkedList<Integer> skipElse = makeList(quads.getNextQuad());
                @SuppressWarnings("unchecked")
                LinkedList<Integer>EFalse = (LinkedList<Integer>) semanticStack.peek();
                semanticStack.push(skipElse);
                generate("GOTO");
                backPatch(EFalse, quads.getNextQuad());
                break;
            }

            case 28:{
                @SuppressWarnings("unchecked")
                LinkedList<Integer> skipElse = (LinkedList<Integer>) semanticStack.pop();
                @SuppressWarnings("unchecked")
                LinkedList<Integer> EFalse = (LinkedList<Integer>) semanticStack.pop();
                @SuppressWarnings("unchecked")
                LinkedList<Integer> ETrue = (LinkedList<Integer>) semanticStack.pop();
                backPatch(skipElse, quads.getNextQuad());
                break;
            }

            case 29:{
                @SuppressWarnings("unchecked")
                LinkedList<Integer> EFalse = (LinkedList<Integer>) semanticStack.pop();
                @SuppressWarnings("unchecked")
                LinkedList<Integer> ETrue = (LinkedList<Integer>) semanticStack.pop();
                backPatch(EFalse, quads.getNextQuad());
                break;
            }

            case 30: {
                if(lookup(token.getVal()) == null){
                    throw SemanticError.VariableNotFoundException(token, lineNumber, line);
                }
                else{
                    semanticStack.push(lookup(token.getVal()));
                }
                semanticStack.push(EType.arithmetic);
                break;
            }

            case 31:{
                //function variable should be here
                EType etype1 = (EType)semanticStack.pop();
                SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
                //pop offset
                SymbolTableEntry offset = (SymbolTableEntry)semanticStack.pop();
                //IS THIS RIGHT?
                //EType etype2 = (EType)semanticStack.pop();
                SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();
                if(typeCheck(id1, id2) == 3 || etype1 != EType.arithmetic){
                    throw SemanticError.GenericError();
                }
                else if(typeCheck(id1, id2)==2){
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
                    generate("STOR",id2,offset,id1);
                }
                break;
            }

            case 32:{
                EType etype = (EType)semanticStack.pop();
                if(etype != EType.arithmetic){
                    throw SemanticError.IllegalETypeException();
                }
                if(!(((SymbolTableEntry)semanticStack.peek()).isArray())){
                    throw SemanticError.IllegalETypeException();
                }
                break;
            }

            case 33:{
                EType eType = (EType)semanticStack.pop();
                SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                ArrayEntry arrayName = (ArrayEntry)semanticStack.peek();

                if(eType != EType.arithmetic){
                    throw SemanticError.IllegalETypeException();
                }
                if(id.getType() != TokenType.INTEGER){
                    throw SemanticError.IllegalIndexException(lineNumber, line);
                }
                else {
                    SymbolTableEntry temp = createTemp(TokenType.INTEGER);
                    generate("SUB", id, arrayName.getLowerBound(),temp);
                    //semanticStack.push(EType.arithmetic);
                    semanticStack.push(temp);
                }
                //semanticStack.push(EType.arithmetic);
                break;
            }

            case 34:{
                EType etype = (EType)semanticStack.pop();
                if(((SymbolTableEntry)semanticStack.peek()).isFunction()){
                    //this.Execute(SemanticAction.action52,token, lineNumber, line);
                }
                else{
                    semanticStack.push(new ConstantEntry("null", TokenType.INTCONSTANT));
                }
                break;
            }

            case 35:{
                parmCountStack.push(0);
                SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
                if(id instanceof ProcedureEntry){
                    //nextParm = ((ProcedureEntry) id).getParameters(0);
                    nextParmStack.push(((ProcedureEntry)id).getParamList());
                    nextParm = nextParmStack.peek().get(0);
                }
                else {
                    //nextParm = ((FunctionEntry) id).getParameters(0);
                    nextParmStack.push(((FunctionEntry)id).getParamList());
                    nextParm = nextParmStack.peek().get(0);
                }
                break;
            }

            case 36:{
                //semanticStack.pop();
                ProcedureEntry id = (ProcedureEntry)semanticStack.pop();
                if(id.getNumberOfParameters() != 0){
                    throw SemanticError.GenericError();
                }
                generate("call",id,"0");
                break;
            }

            case 37:{
                EType eType = (EType)semanticStack.pop();
                if(eType != EType.arithmetic){
                    throw SemanticError.IllegalETypeException();
                }
                SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                if(!(!id.isVariable() || !id.isConstant() || !id.isArray() || !id.isResult())){
                    throw SemanticError.GenericError();
                }
                int counter = parmCountStack.pop();
                counter += 1;
                parmCountStack.push(counter);

                //get the proceedure or function from the stack
                SymbolTableEntry procOrFunc = null;
                for(Object stackItem : semanticStack){
                    if(stackItem instanceof ProcedureEntry || stackItem instanceof FunctionEntry){
                        procOrFunc = (SymbolTableEntry)stackItem;
                        break;
                    }
                }
                /*for(int i = semanticStack.size()-1; i > 0; i--){
                    if (semanticStack.get(i) instanceof ProcedureEntry || semanticStack.get(i) instanceof FunctionEntry){
                        procOrFunc = (SymbolTableEntry)semanticStack.get(i);
                        break;
                    }
                }*/

                if(procOrFunc instanceof FunctionEntry) {
                    FunctionEntry funct = (FunctionEntry) procOrFunc;
                    if (!((funct.getName().toLowerCase().equals("read")) || funct.getName().toLowerCase().equals("write"))) {
                        if (counter > ((FunctionEntry) funct).getNumberOfParameters()) {
                            throw SemanticError.GenericError();
                        }

                        //if (id.getType() != nextParm.getType()) {
                        if (id.getType() != nextParm.getType() ){
                            throw SemanticError.GenericError();
                        }
                        //if (nextParm.isArray()) {
                        if (nextParm.isArray()) {
                            if (!(((ArrayEntry) id).getLowerBound().getName().equals(nextParm.getLowerBound().getName()) &&
                                    ((ArrayEntry) id).getUpperBound().getName().equals(nextParm.getUpperBound().getName()))) {
                                throw SemanticError.GenericError();
                            }
                        }
                    }
                    //else{
                    //    semanticStack.push(id);
                    //}
                    if(counter < funct.getNumberOfParameters()) {
                        nextParm = nextParmStack.peek().get(counter);
                    }
                }
                else{
                    ProcedureEntry proc = (ProcedureEntry) procOrFunc;
                    if (!((proc.getName().toLowerCase().equals("read")) || proc.getName().toLowerCase().equals("write"))) {
                        if (counter > proc.getNumberOfParameters()) {
                            throw SemanticError.GenericError();
                        }
                        if (id.getType() != nextParm.getType()) {
                            throw SemanticError.GenericError();
                        }
                        if (nextParm.isArray()) {
                            if (!(((ArrayEntry) id).getLowerBound().getName().equals(nextParm.getLowerBound().getName()) &&
                                    ((ArrayEntry) id).getUpperBound().getName().equals(nextParm.getUpperBound().getName()))) {
                                throw SemanticError.GenericError();
                            }
                        }
                        if(counter < proc.getNumberOfParameters()) {
                            nextParm = nextParmStack.peek().get(counter);
                        }
                    }
                    //else{
                    //    semanticStack.push(id);
                   //}
                    //semanticStack.push(id);
                    //nextParm = nextParmStack.peek().get(counter);


                }
                break;
            }

            case 38:{
                EType eType = (EType)semanticStack.pop();
                if(eType != EType.arithmetic){
                    throw SemanticError.IllegalETypeException();
                }
                semanticStack.push(token);
                break;
            }

            case 39:{
                EType eType = (EType)semanticStack.pop();
                SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
                Token operator = (Token)semanticStack.pop();
                SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();

                if(eType != EType.arithmetic){
                    throw SemanticError.IllegalETypeException();
                }
                if(typeCheck(id1, id2)== 2){
                    SymbolTableEntry temp1 = createTemp(TokenType.REAL);
                    generate("LTOF",id2,temp1);
                    generate(token.getVal(), id1, temp1);
                }
                if(typeCheck(id1, id2) == 3){
                    SymbolTableEntry temp1 = createTemp(TokenType.REAL);
                    generate("LTOF",id1,temp1);
                    generate(token.getVal(), temp1, id2);
                }
                else{
                    generate(token.getVal(), id1, id2);
                }
                generate("GOTO");
                LinkedList<Integer> ETrue = makeList(quads.getNextQuad()-2);
                LinkedList<Integer> EFalse = makeList(quads.getNextQuad()-1);
                semanticStack.push(ETrue);
                semanticStack.push(EFalse);
                semanticStack.push(EType.relational);
                break;
            }

            case 40:{
                semanticStack.push(token);
                break;
            }

            case 42:{
                EType eType = (EType)semanticStack.pop();
                if (token.getVal().toLowerCase().equals("or")) {
                    if (eType != EType.relational){
                        throw SemanticError.IllegalETypeException();
                    }
                    LinkedList<Integer> EFalse = (LinkedList<Integer>)semanticStack.peek();
                    backPatch(EFalse, quads.getNextQuad());
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
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E1False = (LinkedList<Integer>) semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E1True = (LinkedList<Integer>) semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E2False = (LinkedList<Integer>) semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E2True = (LinkedList<Integer>) semanticStack.pop();

                    if (operator.getVal().toLowerCase().equals("or")) {
                        LinkedList<Integer> ETrue = merge(E1True, E2True);
                        semanticStack.push(ETrue);
                        semanticStack.push(E2False);
                        semanticStack.push(EType.relational);
                    }
                }
                else{
                    SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();

                    if(eType != EType.arithmetic){
                        throw SemanticError.IllegalETypeException();
                    }
                    if (typeCheck(id1, id2) == 0 ){
                        VariableEntry tempVar = createTemp(TokenType.INTEGER);
                        generate(symToOp(operator), id1, id2, tempVar);
                        semanticStack.push(tempVar);
                    }
                    else if (typeCheck(id1, id2) == 1 ){
                        VariableEntry tempVar = createTemp(TokenType.REAL);
                        generate("F"+symToOp(operator), id1, id2, tempVar);
                        semanticStack.push(tempVar);
                    }
                    else if (typeCheck(id1, id2) == 2 ){
                        VariableEntry tempVar = createTemp(TokenType.REAL);
                        generate("LTOF", id1, id2, tempVar);
                        VariableEntry tempVar2 = createTemp(TokenType.REAL);
                        generate("F" + symToOp(operator), id1, tempVar, tempVar2);
                        semanticStack.push(tempVar2);
                    }
                    else if (typeCheck(id1, id2) == 3){
                        VariableEntry tempVar = createTemp(TokenType.REAL);
                        generate("LTOF", id1, tempVar);
                        VariableEntry tempVar2 = createTemp(TokenType.REAL);
                        semanticStack.push(tempVar);
                        generate("F"+symToOp(operator), tempVar,id2, tempVar2);
                    }
                    semanticStack.push(EType.arithmetic);
                }
                break;
            }

            case 44:{
                EType eType = (EType)semanticStack.pop();
                if(eType == EType.relational){
                    if(token.getVal().equals("and")){
                        @SuppressWarnings("unchecked")
                        LinkedList<Integer> EFalse = (LinkedList<Integer>) semanticStack.pop();
                        @SuppressWarnings("unchecked")
                        LinkedList<Integer> ETrue = (LinkedList<Integer>) semanticStack.peek();
                        backPatch(ETrue, quads.getNextQuad());
                        semanticStack.push(EFalse);
                    }
                }
                //push the operator
                semanticStack.push(token);
                break;
            }

            case 45:{
                EType eType = (EType)semanticStack.pop();
                //pop items from stack until the operator is found
                Stack<Object> tempStack = new Stack<>();
                while(!(semanticStack.peek() instanceof Token)){
                    tempStack.push(semanticStack.pop());
                }
                if(((Token)semanticStack.peek()).getVal().toLowerCase().equals("and")){
                    //return items to stack
                    while(!tempStack.empty()){
                        semanticStack.push(tempStack.pop());
                    }
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E1True = (LinkedList<Integer>) semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E1False = (LinkedList<Integer>) semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E2True = (LinkedList<Integer>) semanticStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Integer> E2False = (LinkedList<Integer>) semanticStack.pop();

                    if(eType != EType.relational){
                        throw SemanticError.IllegalETypeException();
                    }
                    else{
                        LinkedList<Integer> ETrue = E2True;
                        LinkedList<Integer> EFalse = merge(E1False, E2False);
                        semanticStack.push(ETrue);
                        semanticStack.push(EFalse);
                        semanticStack.push(EType.relational);
                    }
                }
                else{
                    //return items to stack
                    while(!tempStack.empty()){
                        semanticStack.push(tempStack.pop());
                    }
                    //EType eType = (EType)semanticStack.pop();
                    SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
                    Token operator = (Token)semanticStack.pop();
                    SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();

                    if(eType != EType.arithmetic){
                        throw SemanticError.IllegalETypeException();
                    }
                    if(typeCheck(id1, id2) != 0 && operator.getVal().equals("mod")){
                        throw SemanticError.ModOperandsException(lineNumber, line);//mismatch
                    }
                    if(typeCheck(id1, id2) == 0){
                        if(operator.getVal().toLowerCase().equals("mod")) {
                            SymbolTableEntry temp1 = createTemp(TokenType.INTEGER);
                            generate("MOVE", id1, temp1);
                            SymbolTableEntry temp2 = createTemp(TokenType.INTEGER);
                            generate("MOVE", temp1, temp2);
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
                    semanticStack.push(EType.arithmetic);
                }
                break;
            }
            case 46:{
                if(token.getType() == TokenType.IDENTIFIER){
                    if(lookup(token.getVal()) == null){
                        throw SemanticError.VariableNotFoundException(token, lineNumber, line);
                    }
                    else{
                        semanticStack.push(lookup(token.getVal()));
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
                    //semanticStack.push(EType.arithmetic);
                }
                semanticStack.push(EType.arithmetic);
                break;
            }

            case 47: {
                EType eType = (EType) semanticStack.pop();
                @SuppressWarnings("unchecked")
                LinkedList<Integer> ETrue = (LinkedList<Integer>) semanticStack.pop();
                @SuppressWarnings("unchecked")
                LinkedList<Integer> EFalse = (LinkedList<Integer>) semanticStack.pop();
                if(eType != EType.relational){
                    throw SemanticError.IllegalETypeException();
                }
                else{
                    LinkedList<Integer> NewETrue = EFalse;
                    LinkedList<Integer> NewEFalse = ETrue;
                    semanticStack.push(NewETrue);
                    semanticStack.push(NewEFalse);
                    semanticStack.push(EType.relational);
                }
                break;
            }

            case 48: {
                SymbolTableEntry offset = (SymbolTableEntry)semanticStack.pop();
                if(!offset.getName().equals("null")){
                    //EType etype = (EType)semanticStack.pop();
                    //
                    if(offset.getType() != TokenType.INTEGER){
                        throw SemanticError.IllegalIndexException(lineNumber, line);
                    }
                    else{
                        SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                        VariableEntry temp1 = createTemp(id.getType());
                        generate("LOAD", id, offset, temp1);
                        semanticStack.push(temp1);
                    }
                }
                semanticStack.push(EType.arithmetic);
                break;
            }

            case 49:{
                EType eType = (EType)semanticStack.pop();
                if(eType != EType.arithmetic){
                    throw SemanticError.IllegalETypeException();
                }
                SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
                if(!id.isFunction()){
                    throw SemanticError.GenericError();
                }
                parmCountStack.push(0);
                //nextParm = ((FunctionEntry)id).getParameters(parmCountStack.peek());
                nextParmStack.push(((FunctionEntry)id).getParamList());
                nextParm = nextParmStack.peek().get(0);
                break;
            }

            case 50:{
                Stack<Object>tempStack = new Stack<>();
                //pop the parameters into a temp stack so they can be processed in the correct order
                while(!semanticStack.empty() && !(semanticStack.peek() instanceof FunctionEntry || semanticStack.peek() instanceof ProcedureEntry)) {
                    tempStack.push(semanticStack.pop());
                }
                while(!tempStack.empty()){
                    SymbolTableEntry id = (SymbolTableEntry)tempStack.pop();
                    localMemory += 1;
                    generate("PARAM", id);
                }
                FunctionEntry function = (FunctionEntry)semanticStack.pop();
                if(parmCountStack.peek() > function.getNumberOfParameters()){
                    //Error should reflect mismatch in parameters
                    throw SemanticError.GenericError();
                }
                generate("CALL", function, Integer.toString(parmCountStack.peek()));
                parmCountStack.pop();
                nextParmStack.pop();
                if(!nextParmStack.empty()) {
                    nextParm = nextParmStack.peek().get(0);
                }
                SymbolTableEntry temp = createTemp(function.getType());
                generate("MOVE", function.getResult().getName(), temp);
                semanticStack.push(temp);
                semanticStack.push(EType.arithmetic);
                break;
            }

            case 51:{
                //EType eType = (EType)semanticStack.pop();
                //ProcedureEntry id = (ProcedureEntry)semanticStack.pop();
                //id may be under the item to read or write
                Stack<Object> tempStack = new Stack<>();
                while(!((SymbolTableEntry)semanticStack.peek()).isProc()){
                    tempStack.push(semanticStack.pop());
                }
                ProcedureEntry id = (ProcedureEntry)semanticStack.pop();
                while(!tempStack.empty()) {
                    semanticStack.push(tempStack.pop());
                }

                if(id.getName().toLowerCase().equals("read")){
                    fiftyOneRead();
                }
                else if(id.getName().toLowerCase().equals("write")){
                    fiftyOneWrite();
                }
                else{
                    if(parmCountStack.get(0) != id.getNumberOfParameters()){
                        throw SemanticError.GenericError();
                    }
                    while(!semanticStack.empty() && semanticStack.peek() instanceof ParamEntry){
                        ParamEntry param = (ParamEntry)semanticStack.pop();
                        localMemory += 1;
                        generate("param", id);

                    }

                }
                generate("CALL", id, Integer.toString(parmCountStack.peek()));
                parmCountStack.pop();
                nextParmStack.pop();
                nextParm = null;
                break;
            }

            case 52:{
                EType eType = (EType)semanticStack.pop();
                FunctionEntry id = (FunctionEntry)semanticStack.pop();
                if(!id.isFunction())
                {
                    throw SemanticError.GenericError();
                }
                if(id.getNumberOfParameters() > 0){
                    throw SemanticError.GenericError();
                }
                generate("CALL", id, "0");
                VariableEntry temp = createTemp(id.getType());
                generate("MOVE", id.getResult(), temp);
                semanticStack.push(temp);
                semanticStack.push(EType.arithmetic);
                break;
            }

            case 53: {
                EType eType = (EType)semanticStack.pop();
                if(((SymbolTableEntry)semanticStack.peek()).isFunction()){
                    SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                    if(!id.getName().equals(currentFunction)){
                        throw SemanticError.GenericError();
                    }
                    else{
                        //semanticStack.push("$$"+id.getName());
                        SymbolTableEntry result = ((FunctionEntry)id).getResult();
                        semanticStack.push(result);
                        semanticStack.push(EType.arithmetic);
                    }
                }
                else{
                    semanticStack.push(EType.arithmetic);
                }
                break;
            }

            case 54:{
                EType eType = (EType)semanticStack.pop();
                if(!((SymbolTableEntry)semanticStack.peek()).isFunction()){
                    throw SemanticError.GenericError();
                }
                //semanticStack.push(eType);
                break;
            }

            case 55:{
                LinkedList <Integer> backPatchList = new LinkedList<>();
                backPatchList.add(globalStore);
                backPatch(backPatchList, globalMemory);
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
        this.dumpStack();
    }

    /**
     * looks up a symbol table entry in the proper symbol table
     * @param keyVal
     * @return
     */
    public SymbolTableEntry lookup(String keyVal){
        SymbolTableEntry returnEntry;
        if(!global){
            returnEntry = localTable.lookup(keyVal);
            if (returnEntry == null){
                returnEntry = globalTable.lookup(keyVal);
            }
        }
        else{
            returnEntry = globalTable.lookup(keyVal);
        }
        return returnEntry;
    }
    private void generate (String tviCode){
        String[] newInstruction = {tviCode,"","",""};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }
    private void generate (String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3) {
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String op3 = genOperand(operand3);
        String[] newInstruction = {tviCode, op1, op2, op3};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, String operand1, String operand2, SymbolTableEntry operand3){
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String op3 = genOperand(operand3);
        String[] newInstruction = {tviCode, op1, op2, op3};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, String operand3){
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String op3 = genOperand(operand3);
        String[] newInstruction = {tviCode, op1, op2, op3};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode,SymbolTableEntry operand1, String operand2){
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String[] newInstruction = {tviCode, op1, op2,""};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode,SymbolTableEntry operand1, String operand2, SymbolTableEntry operand3){
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String op3 = genOperand(operand3);
        String[] newInstruction = {tviCode, op1, op2, op3};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode,SymbolTableEntry operand1, SymbolTableEntry operand2){
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String[] newInstruction = {tviCode, op1, op2,""};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, String operand1, SymbolTableEntry operand2){
        String op1 = genOperand(operand1);
        String op2 = genOperand(operand2);
        String[] newInstruction = {tviCode, op1, op2,""};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();
    }

    public void generate(String tviCode, String operand1){
        String[] newInstruction = {tviCode, operand1,"",""};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();

    }
    public void generate(String tviCode, SymbolTableEntry operand1){
        String op1 = genOperand(operand1);
        String[] newInstruction = {tviCode, op1,"",""};
        quads.addQuad(newInstruction);
        quads.incrementNextQuad();

    }
    public void fiftyOneWrite(){
        while(!semanticStack.empty() && semanticStack.peek() instanceof SymbolTableEntry) {
            SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
            generate("NEWL");
            if (id.getType() == TokenType.REAL) {
                generate("FOUTP", id);
            } else if(id.getType() == TokenType.INTCONSTANT || id.getType() == TokenType.INTEGER) {
                generate("OUTP", id);
            }
            else {
                generate("PRINT", Integer.toString(id.getAddress()));
            }
        }
    }
    public void fiftyOneRead(){
        while(!semanticStack.empty() && semanticStack.peek() instanceof ParamEntry) {
            SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
            semanticStack.pop();
            semanticStack.pop();
            if (id.getType() == TokenType.REAL) {
                generate("FINP", id);
            } else {
                generate("INP", id);
            }
        }
    }

    public String genOperand(SymbolTableEntry operand){
        if (operand.isParam()) {
            return "^%" + Integer.toString(Math.abs(operand.getAddress()));
        }
        else if(operand.isConstant()){
            VariableEntry tempVar = createTemp(operand.getType());
            generate("MOVE", operand.getName(), tempVar);
            if(global){
                return "_" + Math.abs(tempVar.getAddress());
            }
            else{
                return "%" + Math.abs(tempVar.getAddress());
            }
        }
        else{
            if(global){
                return "_" +Integer.toString(Math.abs(operand.getAddress()));
            }
            else {
                return "%" + Integer.toString(Math.abs(operand.getAddress()));
            }
        }
    }
    public String genOperand(String operand){
        //int opLocation = constantTable.lookup(operand).getAddress();
        if(global){
            return "_" + operand;
        }
        else{
            return "%" + operand;
        }
    }
    public void printQuads(){
        this.quads.print();
    }
}

