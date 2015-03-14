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
        String parseFile = "/Src/Test/TestFiles/parsetest.dat";
        String parseTableFile = "/Src/Test/TestFiles/parseTable.dat";
        ParserDriver instance = new ParserDriver(parseFile, parseTableFile);
        assert(!instance.parser.foundErrors());
    }

}