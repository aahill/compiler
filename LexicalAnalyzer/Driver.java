package LexicalAnalyzer;
import CompilerError.*;
import GrammarSymbols.Token;

public class Driver {
    public TokenAssembler lexer;
    private Token token;

    public Driver(String file){
        lexer = new TokenAssembler(file);
    }

    /**
     * attempts to get the next token from the file via the lexical analyzer.
     * @return the next token of the file
     */
    public Token getNextToken(){
        try{
            token = lexer.getNextToken();
        }
        catch (LexicalError ex){
            ex.printStackTrace();
        }
        return token;
    }


}
