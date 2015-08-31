package Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import CompilerError.ParserError;

public class ParseTable {

    /** Stores the final parse table */
    public int[][] table;
    /** Used to read the source code file. */
    private BufferedReader reader = null;

    /**
     * standard constructor for the parse table
     * @param parseTableFile the file location for the parse table
     */
    public ParseTable(String parseTableFile)throws ParserError{
        open(parseTableFile);

        //temporarily stores the rows of values extracted from the file.
        //Used later to correctly size the parse table.
        ArrayList<int[]> tempLineStorage = new ArrayList<>();
        //copy file values into the arraylist
        getNumericalValues(tempLineStorage);

        //height is the number of lines extracted from the parse table file
        int height = tempLineStorage.size();
        //width is the length of the first line from the parse table file
        int width = tempLineStorage.get(0).length;
        table = new int[width][height];

        /*copy the values from tempLineStorage to the parse table*/
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                table[x][y] = tempLineStorage.get(y)[x];
            }
        }

    }


    /**
     * Attempts to open the given file. Returns true if the file was opened
     * successfully, or false otherwise.
     *
     * @param filename String The name of the file to open
     * @return boolean True if the file was opened, false otherwise.
     */
    private boolean open(String filename)throws ParserError{
        try{
            reader = new BufferedReader(new FileReader(filename));
        }
        catch (IOException ex){
            ex.printStackTrace(System.out);
            reader = null;
            throw ParserError.ParseTableErrorException();
        }
        return reader != null;
    }

    /**
     * extracts the numerical values from the file and converts them into float values
     * @return an ArrayList that contains the numerical values from the parse table file
     */
    private ArrayList getNumericalValues(ArrayList lineStorage){
        String currLine;
        /**
         * for every line in the file, take the line and split it using blank space as a delimiter
         * Convert every string into an integer and append it to the ArrayList, and then return it
         */
        try {
            while ((currLine = reader.readLine()) != null) {

                //only read lines that contain values
                if(!currLine.isEmpty()) {
                    //trim whitespace and split on whitespace
                    String[] parse_table_line = currLine.trim().split("\\s+");
                    //convert string values into floats and store
                    int[] convertedStrings = new int[parse_table_line.length];
                    for (int i = 0; i < convertedStrings.length; i++) {
                        convertedStrings[i] = Integer.parseInt(parse_table_line[i]);
                    }
                    //add array of converted digits to the arrayList
                    lineStorage.add(convertedStrings);
                }

            }
            reader.close();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        return lineStorage;
    }
}
