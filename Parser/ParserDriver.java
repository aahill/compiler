package Parser;

import CompilerError.*;


public class ParserDriver {

    public Parser parser;

    public ParserDriver(String inputFilename, String parseTableFilename)
    {
        parser = new Parser(inputFilename, parseTableFilename);
    }

    public void run()
    {
        //try {
            parser.parse();
        /*}

        catch(CompilerError ex) { //SHOULD BE PARSER ERROR
            System.err.println(ex.getMessage());
            //parser.semanticActions.quads.print();
            System.exit(1);

        }*/

        //checks if the parser found errors; if not, print parse success message
        if(!parser.foundErrors()) {
            System.out.println("Compilation successful.");
        }
    }


    public static void main(String[] args)
    {
        String parseTableFile = "Src/Test/TestFiles/parseTable.dat";
        //String toParse ="Src/Test/TestFiles/semanticActiontest1.dat";
        //String toParse ="Src/Test/TestFiles/boolTest.dat";
        //String toParse ="Src/Test/TestFiles/procTest.dat";
        //String toParse = "Src/Test/TestFiles/expressionTest.dat";
        //String toParse = "Src/Test/TestFiles/array.pas";
        //String toParse = "Src/Test/TestFiles/ifTest.dat";
        //String toParse = "Src/Test/TestFiles/whileTest.pas";
        String toParse = "Src/Test/TestFiles/ultimateTest.dat";
        ParserDriver test = new ParserDriver(toParse, parseTableFile);
        test.run();
        test.parser.semanticActions.printQuads();
        //test.parser.semanticActions.localTable.dumpTable();
        //test.parser.semanticActions.dumpSemanticStack();
    }
}