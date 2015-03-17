package Test;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import LexicalAnalyzer.*;
import GrammarSymbols.*;
import CompilerError.*;
import org.junit.rules.ExpectedException;

import java.net.URL;
public class TokenAssemblerTest {

    /**
     * test the token assembler on the provided lexTest file
     */
    @Test
    public void testOnLexTest(){
        String testFile = "Src/Test/TestFiles/errorFreelextest.dat";
        TokenAssembler instance = new TokenAssembler(testFile);
        //the sequence of correct tokens, sans exception-throwing lines
        TokenType[] expectedTokens = {TokenType.PROGRAM, TokenType.BEGIN, TokenType.END, TokenType.VAR,
                TokenType.FUNCTION, TokenType.PROCEDURE, TokenType.DOUBLEDOT, TokenType.RESULT, TokenType.INTEGER,
                TokenType.REAL, TokenType.ARRAY, TokenType.OF, TokenType.IF, TokenType.THEN, TokenType.ELSE,
                TokenType.WHILE, TokenType.DO, TokenType.NOT, TokenType.ADDOP, TokenType.RELOP, TokenType.RELOP,
                TokenType.RELOP, TokenType.RELOP, TokenType.RELOP, TokenType.UNARYPLUS, TokenType.IDENTIFIER,
                TokenType.ADDOP, TokenType.UNARYMINUS, TokenType.RESULT, TokenType.END, TokenType.DOUBLEDOT,
                TokenType.REALCONSTANT, TokenType.IDENTIFIER, TokenType.ENDMARKER, TokenType.REALCONSTANT,
                TokenType.REALCONSTANT, TokenType.INTCONSTANT, TokenType.ADDOP, TokenType.IDENTIFIER, TokenType.ADDOP,
                TokenType.INTCONSTANT, TokenType.ENDMARKER, TokenType.INTCONSTANT, TokenType.RIGHTPAREN,
                TokenType.SEMICOLON, TokenType.MULOP, TokenType.MULOP, TokenType.MULOP /*(DIV)*/, TokenType.MULOP/*(MOD)*/,
                TokenType.MULOP /*(AND)*/, TokenType.ASSIGNOP, TokenType.COLON, TokenType.COLON, TokenType.RELOP,
                TokenType.INTCONSTANT /*(+123)*/, TokenType.LEFTPAREN, TokenType.LEFTBRACKET, TokenType.RIGHTBRACKET,
                TokenType.DOUBLEDOT, TokenType.UNARYPLUS, TokenType.INTCONSTANT /*(-46)*/, TokenType.IDENTIFIER/*(aname)*/,
                TokenType.IDENTIFIER /*(anothername)*/,TokenType.REALCONSTANT/*(123.456)*/, TokenType.ENDMARKER,
                TokenType.INTCONSTANT/*123456*/,TokenType.IDENTIFIER/*(en)*/, TokenType.IDENTIFIER/*(d)*/,
                TokenType.LEFTBRACKET, TokenType.INTCONSTANT/*(5)*/, TokenType.DOUBLEDOT, TokenType.INTCONSTANT/*(9)*/,
                TokenType.RIGHTBRACKET, TokenType.INTCONSTANT/*(4)*/, TokenType.IDENTIFIER/*(abc)*/,TokenType.ENDMARKER,
                TokenType.INTCONSTANT, TokenType.IDENTIFIER, TokenType.ENDOFFILE};

        //continually get the tokens from the file and compare them against the expected token sequence
        for(int i = 0; i < expectedTokens.length; i++) {
            try {
                Token nextToken = instance.getNextToken();
                assert (nextToken.getType() == expectedTokens[i]);
            }
            catch(LexicalError ex){
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }

    }

    /**
     * tests the getNextChar function on a simple file
     * @throws LexicalError
     */
    @Test
    public void testGetNextChar()throws LexicalError {
        System.out.println("getNextChar test");
        //TEST1: test getNextChar() on a normal file
        //String file1 = testDataRoot + "\"hello world\".dat";
        String file = "/Users/Aaron/Projects/compiler/src/Test/TestFiles/hello world.dat";
        TokenAssembler instance = new TokenAssembler(file);
        instance.getNextChar();
        //we will test getNextChar consecutively against the entirety of the
        //file
        char[] expectedChars = {'h','e','l','l','o',' ','w','o','r','l','d'};
        for(int i = 0; i < expectedChars.length; i++) {
            assertEquals(instance.currChar,expectedChars[i]);
            instance.getNextChar();
        }

    }
    /**
     * tests getNextChar() on a file containing a single invalid character
     */

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Test
    public void testOnInvalid()throws LexicalError{
        exception.expect(LexicalError.class);
        String file = "/Users/Aaron/NetBeansProjects/Compiler/testData/^.dat";
        TokenAssembler instance = new TokenAssembler(file);
        instance.getNextToken();
    }

}