package Test;

import org.junit.Test;
import Parser.*;

import static org.junit.Assert.*;

public class ParserTest {

    /**
     * tests parser on parsetest.dat to ensure proper parsing
     */
    @Test
    public void testOnParseTest(){
        String parseTableFile = "Src/Test/TestFiles/parseTable.dat";
        String parseFile = "Src/Test/TestFiles/parseTest.dat";
        ParserDriver instance = new ParserDriver(parseFile, parseTableFile);
        instance.run();
        assert(!instance.parser.foundErrors());
    }

}