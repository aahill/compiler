package Parser;

import CompilerError.ParserError;
import CompilerError.SemanticError;
import SemanticActions.SemanticActions;
import GrammarSymbols.*;
import LexicalAnalyzer.*;
import java.util.Stack;
import GrammarSymbols.Token;

public class Parser {
    private Driver lexDriver;
    private Stack<GrammarSymbol> stack;
    private Token currentToken;
    private GrammarSymbol predicted;
    private ParseTable parseTable;
    private RHSTable rhsTable;
    public SemanticActions semanticActions;
    //determines if the parser should run methods aiding debugging (i.e. dumping the stack)
    private boolean testMode;
    //determines whether the parser detected errors during the parse
    private boolean foundErrors;

    /**
     * standard constructor for the parser, including initializations and testMode set to false
     * @param inputFile the filepath for the "code" to be parsed
     * @param parseTableFile the filepath to read the parse table from
     */
    public Parser(String inputFile, String parseTableFile){
        //initializations
        this.semanticActions = new SemanticActions();
        this.lexDriver = new Driver(inputFile);
        this.stack = new Stack<>();
        try {
            this.parseTable = new ParseTable(parseTableFile);
        }
        catch(ParserError error){
            System.err.println(error.getMessage());
        }
        this.rhsTable = new RHSTable();
        this.currentToken = lexDriver.getNextToken();
        //testMode not enabled on default constructor
        this.testMode = false;
        //initially no errors are found in the parse
        this.foundErrors = false;

        //initially push the distinguished symbol of the grammar (<Goal>) 
        //then push the ENDOFFILE terminal symbol, so that it is beneath the goal symbol
        this.stack.push(TokenType.ENDOFFILE);
        this.stack.push(NonTerminal.Goal);
    }

    /**
     * overloaded constructor allows for the enabling of debugging methods by setting the testing parameter to 'true'
     * @param inputFile the filepath for the "code" to be parsed
     * @param parseTableFile the filepath to read the parse table from
     * @param testing the boolean to either enable or disable testing.
     *                Note that testing is disabled in the default constructor
     */
    public Parser(String inputFile, String parseTableFile, boolean testing){
        Parser parser = new Parser(inputFile,parseTableFile);
        parser.setTestMode(testing);
    }

    public void parse(){
        while(!stack.empty()){
            try {
                parseNextItem();
            }
            catch(ParserError error){
                System.err.println(error.getMessage());
                currentToken = lexDriver.getNextToken();
            }
            catch(SemanticError error){
                System.err.println(error.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * sets the testMode of the parser
     * @param newTestMode enables or disables the parser's testMode,
     *                    granting or blocking access from debugging functions
     */
    public void setTestMode(boolean newTestMode){
        this.testMode = newTestMode;
    }

    /**
     * returns foundErrors, which records whether errors were found in the parse
     */
    public boolean foundErrors(){
        return this.foundErrors;
    }

    public void parseNextItem()throws ParserError, SemanticError{
        predicted = stack.peek();
        /**
         * if the predicted symbol is a non-terminal, index into the parse table to determine
         * if the production is valid.
         */
        if(predicted.isNonTerminal()){
            //get the index of the non-terminal to index into the parse table
            int predictedIndex = ((NonTerminal) predicted).getIndex();
            //get the index of the token to index into the parse table
            int tokenIndex = currentToken.type.getIndex();
            //index into parse table using predicted and token indices
            int productionNumber = parseTable.table[predictedIndex][tokenIndex];

            //if the production number is 999, then we have encountered an unexpected token
            if(productionNumber == 999){
                foundErrors = true;
                //in order to try and continue analyzing the file for errors, get the next token and try to continue parsing
                //currentToken = lexDriver.getNextToken();
                throw ParserError.UnexpectedTokenException(currentToken, lexDriver);
            }
            else{
                stack.pop();
            }
            /*negative numbers correlate to the empty string, so nothing should be pushed to the stack
            otherwise, the production is valid and we must push the right hand symbols onto the stack*/
            if (productionNumber > 0){
                //index into the rhs table to get the correct set of symbols for the production
                GrammarSymbol[] production = rhsTable.rules[productionNumber];
                /*production symbols are sequenced in the array left to right. To push them onto the stack in the
                correct order, the list must be iterated through in reverse order*/
                for(int i = production.length-1; i >= 0; i--){
                    stack.push(production[i]);
                }
            }
        }
        else if(predicted.isToken()){
            //try a match move
            //the predicted token type matched the token, continue to next token
            if(predicted == currentToken.getType()){
                stack.pop();
                //to ensure that possible semantic actions get called on the correct token, the next stack item
                // (assuming the stack is not empty) must be checked, and executed if necesarry
                if(!stack.empty()) {
                    if (stack.peek().isAction()) {
                        executeAction();
                    }
                }
                currentToken = lexDriver.getNextToken();
                //once the token is matched, the next stack symbol should be evaluated

            }
            //otherwise there is a mismatch, and an error must be thrown
            else{
                //panicMode();
                //if test mode is enabled, then dump the stack
                /*if(testMode) {
                    dumpStack();
                }*/
                //get rid of the bad token
                foundErrors = true;
                //in order to try and continue analyzing the file for errors, get the next token and try to continue parsing
                //currentToken = lexDriver.getNextToken();
                throw ParserError.TokenMismatchException(predicted, currentToken, lexDriver);
            }
        }
        //If the next item is a semantic action, it must be executed and then popped off of the stack
        else if(predicted.isAction()){
            try {
                semanticActions.Execute((SemanticAction) predicted, currentToken);
                stack.pop();

            }
            catch(SemanticError semanticError){
                System.err.print(semanticError.getMessage());
            }

        }

    }

    public void executeAction() throws SemanticError{
        predicted = stack.peek();
        //try {
            semanticActions.Execute((SemanticAction) predicted, currentToken);
        //}
        //catch (SemanticError semanticError) {
        //    System.err.print(semanticError.getMessage());
        //}
        stack.pop();
    }

    /**
     * if a mismatch occurs between the predicted token and the token read in from the lexer, the parser will
     * pop tokens off until it reaches a semicolon. It will then continually request new tokens from the lexer until it
     * too reaches a semicolon. If neither of these conditions are met before the end of file are reached or the
     * stack is empty, then a PanicModeFailure exception will be thrown.
     */
    public void panicMode()throws Exception{
        System.out.println("ERROR:  ATTEMPTING TO CONTINUE PARSE");
        while(predicted != TokenType.SEMICOLON && predicted != TokenType.ENDOFFILE) {
            predicted = stack.pop();
        }
        while(currentToken.getType() != TokenType.SEMICOLON && currentToken.getType() != TokenType.ENDOFFILE){
            currentToken = lexDriver.getNextToken();
        }
        //TODO: define panicMode exception
        //if the stack is empty, or if either the current token or predicted token are not semicolons, then panic mode
        //has failed
        if (currentToken.getType() != TokenType.SEMICOLON || predicted == TokenType.SEMICOLON || stack.isEmpty()){
            throw new Exception("Panic mode failed");
        }

    }

    /**
     * dumps the contents of the stack
     */
    public void dumpStack(){
        System.out.println("DUMPING STACK!\n +" + "/------------------------------");
        int stackIndex = 0;
        while(!stack.isEmpty()){
            System.out.println("[stack item "+ stackIndex+ "] " + stack.pop());
            stackIndex += 1;
        }
    }
}
