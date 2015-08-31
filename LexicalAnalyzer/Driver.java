package LexicalAnalyzer;
import CompilerError.*;
import GrammarSymbols.Token;

/**
 *
 * @author Aaron
 */
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

    /**
     * @param args the command line arguments
     */
    
    /*public static void main(String[] args) {
        // file to test lexer on
        //String testFile = "/Users/Aaron/Downloads/lextest1.dat";
        String testFile = "/Users/Aaron/NetBeansProjects/Compiler/testData/lextest.dat";
        
        TokenAssembler lexer = new TokenAssembler(testFile);
        Token curr_token = new Token(TokenType.IDENTIFIER,"");
        while(curr_token.getVal() != "EOF"){
            try{
                curr_token = lexer.getNextToken();
                System.out.println(curr_token);
            }
            catch (LexicalError ex){
                //ex.printStackTrace();
                //System.exit(1);
            }
        }      
    }*/

}
