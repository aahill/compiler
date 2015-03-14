package Test;

import static org.junit.Assert.*;
import org.junit.Test;
import Parser.*;
import CompilerError.ParserError;

public class ParseTableTest {
    /**
     * tests the parse table construction by instantiating one and checking its initial([0][0] value)
     */
    @Test
    public void testParseTableConstruction(){
        String fileLocation = "/Users/Aaron/Projects/compiler/src/Test/TestFiles/parseTable.dat";
        try {
            ParseTable instance = new ParseTable(fileLocation);
            //ensure the first number in the parse table is 001
            assert(instance.table[0][0] == 1);
        }
        catch(ParserError error){
            System.err.println(error.getMessage());
        }


    }

}


