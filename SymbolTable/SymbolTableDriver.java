package SymbolTable;

import CompilerError.LexicalError;
import GrammarSymbols.*;
import LexicalAnalyzer.*;

public class SymbolTableDriver {

    public SymbolTableDriver() {
        super();
    }

    protected void run(String filename) {
        SymbolTable KeywordTable = new SymbolTable(17);
        SymbolTable GlobalTable = new SymbolTable(37);
        SymbolTable ConstantTable = new SymbolTable(37);

        // TODO: add read, write, main to global table

        // Develop and use a routine to fill the KeywordTable, if appropriate

        TokenAssembler tokenizer =
                new TokenAssembler(filename);

        Token token;

        try {
            token = tokenizer.getNextToken();

            while (!(token.getType() == TokenType.ENDOFFILE)) {

                if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)) {
                    // If the token is a constant, add it to constantTable
                    ConstantTable.insert(new ConstantEntry(token.getVal(), token.getType()));
                } else if (token.getType() == TokenType.IDENTIFIER) {

                    //  If it is an identifier add it to Global table
                    // as a variable entry
                    GlobalTable.insert(new VariableEntry(token.getVal(), token.getType()));

                }
                token = tokenizer.getNextToken();
            }
        } catch (LexicalError ex) {
            System.err.println(ex);
        }

//		KeywordTable.dumpTable();
        GlobalTable.dumpTable();
        ConstantTable.dumpTable();
    }


    public static void main(String[] args) {
        SymbolTableDriver test = new SymbolTableDriver();
        test.run("Src/Test/TestFiles/symtabtest.dat");
    }


}