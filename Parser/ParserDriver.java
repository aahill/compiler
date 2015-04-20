package Parser;

public class ParserDriver {

    public Parser parser;

    public ParserDriver(String inputFilename, String parseTableFilename)
    {
        parser = new Parser(inputFilename, parseTableFilename);
    }

    public void run()
    {
        try {
            parser.parse();
        }
        catch(Exception ex) { //SHOULD BE PARSER ERROR
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        //checks if the parser found errors; if not, print parse success message
        if(!parser.foundErrors()) {
            System.out.println("Compilation successful.");
        }
    }


    public static void main(String[] args)
    {
        String parseTableFile = "Src/Test/TestFiles/parseTable.dat";
        //String toParse = "Src/Test/TestFiles/parseTest.dat";
        String toParse = "Src/Test/TestFiles/semanticActiontest1.dat";
        ParserDriver test = new ParserDriver(toParse, parseTableFile);
        test.run();
    }
}
