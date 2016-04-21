package Parser;

import CompilerError.*;

/**
  * the parser driver loads the file, and begins the compiling process
**/
public class ParserDriver {

    public Parser parser;

    public ParserDriver(String inputFilename, String parseTableFilename)
    {
        parser = new Parser(inputFilename, parseTableFilename);
    }

    /*
    * begin parsing the file
    */
    public void run()
    {
            parser.parse();

        //checks if the parser found errors; if not, print parse success message
        if(!parser.foundErrors()) {
            System.out.println("Compilation successful.");
        }
    }

    //loads appropriate file and attempts compilatio
    public static void main(String[] args)
    {
        //load parse table for grammer construction
        String parseTableFile = "Test/TestFiles/parseTable.dat";
        //multiple files for testing given below

        //String toParse ="Test/TestFiles/semanticActiontest1.dat";
        //String toParse ="Test/TestFiles/boolTest.dat";
        //String toParse ="Test/TestFiles/procTest.dat";
        //String toParse = "Test/TestFiles/expressionTest.dat";
        //String toParse = "Test/TestFiles/array.pas";
        //String toParse = "Test/TestFiles/ifTest.dat";
        //String toParse = "Test/TestFiles/whileTest.pas";
        String toParse = "Test/TestFiles/ultimateTest.dat";
        ParserDriver test = new ParserDriver(toParse, parseTableFile);
        test.run();
        test.parser.semanticActions.printQuads();
    }
}