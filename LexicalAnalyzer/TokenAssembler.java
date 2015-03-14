package LexicalAnalyzer;

import CompilerError.*;
import GrammarSymbols.Token;
import GrammarSymbols.TokenType;

/**
 * continuously gets the next character from CharStream and appends it into a
 * name, starting a new lexeme when it encounters a delimiting character.
 * It will then return this lexeme as a string
 */
public class TokenAssembler {
    public final CharStream charStream;

    public TokenAssembler(String dataFile){
        this.charStream = new CharStream(dataFile);
    }


    //these characters determine when we encounter a new lexeme
    //TODO: Improve delimiter checking by breaking characters into smaller sets
    public static final char[] operaters = {'+','-','*','/','='};
    public static final char[] seperators = {';',':','(',')','[',']'};
    public static final char[] delimiters = {'+','-','*','/','=',';',':','(',')',' ','<','>','[',']',','};
    public char prevChar = ' ';
    public char currChar = ' ';
    public String prevLexeme = "";
    public Token currToken;
    public Token prevToken;
    //CharStream charStream = new CharStream(toRead);
    //sets the maximum allowable length of identifier lexemes
    int LEXEME_MAX_LENGTH = 32;
    /**
     * determines if a given character is in the set of delimiters
     * @param character the character to check against the array of delimiters
     * @return true if the character is a delimiter
     */
    public boolean delimiterCheck(char character){
        for (char delim : delimiters){
            if (delim == character)
                return true;
        }
        return false;
    }
    /**
     * determines if a lexeme is a number, using a regular expression
     * @param lexeme the lexeme to match against the regular expression
     * @return whether the number matches the given regular expression
     */
    public boolean isNumber(String lexeme){
        return lexeme.matches("(\\+|-)?\\d*\\.?\\d+");
    }
    /**
     * pushes the current character back to the charStream to be read as the
     * next character.sets current character to the previous character,
     * and previous character to blank.
     */
    public void pushBackCurrChar(){
        charStream.pushBack(currChar);
        currChar = prevChar;
        prevChar = ' ';
    }

    /**
     * clears the stored current char and previous char
     */
    public void clearCharBuffer(){
        prevChar = ' ';
        currChar = ' ';
    }

    /**
     * read in the next character from file and store as currChar.
     * previous character becomes prevChar
     */
    public void getNextChar() throws LexicalError{
        try{
            prevChar = currChar;
            currChar = charStream.currentChar();
        }
        catch (LexicalError ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
    /**
     * reads in the next non blank (' ') char from charStream
     * stores as currChar
     */
    public void getNextSignificantChar(){
        char aChar = ' ';
        try{
            while (aChar == ' '){
                aChar = charStream.currentChar();
            }
            prevChar = currChar;
            currChar = aChar;
        }
        catch (LexicalError ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    public Token getNextToken()throws LexicalError{
        String lexeme = "";
        clearCharBuffer();
        /*get initial character if the character is a blank. If it isn't,
        * the char was seen seen while assembling a previous lexeme, but may
        belong to the lexeme to be assembled*/
        getNextSignificantChar();

        //the method will return a basic token, with a null token type that will later be
        //turned into a more specific token
        currToken = new Token(TokenType.NULL,"");

        //if the initial character is a letter, then the lexeme is either a
        //identifier or keyword
        if (Character.isLetter(currChar)){
            try{
                lexeme = assembleKwd_IDLexeme();
            }
            catch(LexicalError ex){
                System.err.println(ex.getMessage());
                //System.exit(1);
            }
        }

        //if the initial character is a digit, the lexeme is either an int or
        //real number
        else if (Character.isDigit(currChar)){
            try{
                lexeme = assembleNumericalLexeme();
            }
            catch(LexicalError ex){
                System.err.println(ex);
                System.exit(1);
            }
        }

        else if (currChar == CharStream.EOF){
            lexeme = "EOF";
        }

        else{
            lexeme = assembleSymbolLexeme();
        }

        //set the token value to be the lexeme
        currToken.setVal(lexeme);
        //call the token to reclassify itself if it hadn't ben given a type previously
        if(currToken.type == TokenType.NULL){
            currToken.reclassify();
        }
        prevToken = currToken;
        return currToken;
    }

    public String assembleSymbolLexeme()throws LexicalError{
        String currLexemeString = "";
        //the various rules for determining if a lexeme consists of one or more
        //symbols.

        //determine if lexeme is '>' or '>='
        if (currChar == '>'){
            currLexemeString += currChar;
            getNextChar();
            if (currChar == '='){
                currLexemeString += currChar;
            }
            else{
                //otherwise, the lexeme is '>' and the current char will be
                //evaluated on the next pass
                charStream.pushBack(currChar);
                currChar = prevChar;
                prevChar = ' ';
            }
        }
        //determine if lexeme is '<', '<>', or '<='
        else if (currChar == '<'){
            currLexemeString += currChar;
            getNextChar();
            if (currChar == '>' || currChar == '='){
                currLexemeString += currChar;
            }
            //the lexeme is '<', and the current char will be evaluated on the
            //next pass
            else{
                charStream.pushBack(currChar);
                currChar = prevChar;
            }
        }
        //if the char is '*' or '/' then it will become the returned lexeme
        else if (currChar == '*' || currChar == '/' ||
                currChar == '(' || currChar == ')' || currChar == '=' ||
                currChar == '[' || currChar == ']' || currChar == ';'){
            currLexemeString += currChar;
        }
        //if the char is ':' then it's either a part of ':=' or simply ':'
        else if (currChar == ':'){
            getNextChar();
            if (currChar == '='){
                currLexemeString += prevChar;
                currLexemeString += currChar;
            }
            else{
                currLexemeString += prevChar;
                pushBackCurrChar();
            }
        }
        else if (currChar == ','){
            currLexemeString += currChar;
        }
        //if the char is '+' or '-' then we must check the previous token to
        //determine if the symbol defines a number's positivity/negativity,
        //or is for an arithmetic expression
        else if (currChar == '+' || currChar == '-'){
            //using a regex to determine if the previous lexeme was an integer
            //or a real number. If it is, then the symbol is an arithmetic
            //expression

            getNextSignificantChar();
            if (!isNumber(prevLexeme) && prevToken.type != TokenType.IDENTIFIER && Character.isDigit(currChar)){
                //if (prevToken.type != TokenType.IDENTIFIER && Character.isDigit(currChar)){
                currLexemeString += prevChar;
                //currLexemeString += currChar;
                //getNextSignificantChar();
                currLexemeString += assembleNumericalLexeme();
            }
            else{
                //otherwise the symbol is either an addop or a unary plus/minus
                currLexemeString += prevChar;
                //push back the sequential symbol to ensure it doesn't get
                //stepped on
                pushBackCurrChar();
                //check to see if the conditions are met for the symbol to be a unary operator
                if (prevToken.type != TokenType.INTCONSTANT && prevToken.type != TokenType.REALCONSTANT
                        && prevToken.type != TokenType.IDENTIFIER && prevToken.type != TokenType.RIGHTBRACKET
                        && prevToken.type != TokenType.RIGHTPAREN){
                    //if so, if the character is a plus, it's unaryplus
                    if(currChar == '+'){
                        currToken.type = TokenType.UNARYPLUS;
                    }
                    //otherwise it must be a minus, and is unaryminus
                    else{
                        currToken.type = TokenType.UNARYMINUS;
                    }
                }
            }
        }
        else if (currChar == '.'){
            //real numbers must start with a digit.
            //Therefore if the previous char is a ' ' then we must return the '.'
            //as its own character and process the rest of the 
            if (!Character.isDigit(prevChar)){
                //check to determine if the lexeme is the double dot ".."
                getNextChar();
                if(currChar == '.'){
                    currLexemeString += currChar;
                    currLexemeString += prevChar;
                }
                //if it isn't, then the '.' becomes its own lexeme, and the 
                //sequential characters will be pushed back
                else{
                    currLexemeString += prevChar;
                    pushBackCurrChar();
                }
            }
            //otherwise we must get the next character to determine what the lexeme is
            else{
                getNextChar();
                //if the current and previous characters are '.'s, then the
                //lexeme is '..'
                if (currChar == '.'){
                    currLexemeString +=currChar;
                    currLexemeString += prevChar;
                    //return currLexemeString;
                }

                //if a digit directly follows the '.' character, then the lexeme
                //is a real number
                else if (Character.isDigit(currChar)){
                    //currLexemeString += prevChar;
                    //currLexemeString += currChar;
                    pushBackCurrChar();
                    currLexemeString += assembleNumericalLexeme();

                }
                //if there is blank space after the '.' then the '.' will be
                //assembled as its own lexeme
                else if (currChar == ' '){
                    currLexemeString += prevChar;
                }
                else{
                    currLexemeString += prevChar;
                    pushBackCurrChar();
                }
            }
        }
        return currLexemeString;
    }

    /**
     * Assembles a keyword or identifier lexeme.
     * If the initial char is a letter, then the lexeme must be either a
     * keyword or identifier.
     * @throws LexicalError if the lexeme is longer than the max allowed length
     * @return the assembled id lexeme
     */
    public String assembleKwd_IDLexeme()throws LexicalError{
        String lexemeString = "";
        //check for a delimiter, signifying the end of the lexeme
        while (!delimiterCheck(currChar) && currChar != '.'){
            // identifier lexemes cannot be longer than 32 characters
            if (lexemeString.length() > LEXEME_MAX_LENGTH){
                throw LexicalError.IdentiferTooLong(charStream.lineNumber(),
                        lexemeString);
            }
            lexemeString += currChar;
            getNextChar();
        }
        //if the delimiter is significant (not a blank) then we must push it
        //back to be examined as the next lexeme
        if(currChar != ' '){
            pushBackCurrChar();
        }
        return lexemeString;
    }

    /**
     * Assembles a numerical lexeme.
     * If the initial char is a digit, then the lexeme must be a
     * numerical constant (integer or real number), or potentially a malformed
     * identifier.
     * @throws LexicalError if it is determined that the lexeme is an identifier
     * and is over the max allowable identifier size
     * @return the assembled id lexeme
     */
    public String assembleNumericalLexeme()throws LexicalError{
        String lexemeString = "";
        //check for a delimiter, signifying the end of the lexeme
        //also check for a lexeme that requires
        while (!delimiterCheck(currChar)){
            //a constant cannot be longer than the predefined max length
            if(lexemeString.length() > LEXEME_MAX_LENGTH){
                throw LexicalError.ConstantTooLong(charStream.lineNumber(),
                        lexemeString);
            }
            if (Character.isDigit(currChar)){
                lexemeString += currChar;
                getNextChar();
            }
            //NOTE: one occurance of the letter 'e', along with *ONLY* digits
            //is permissable for the purpose of exponential notation
            else if (currChar == 'e' && lexemeString.indexOf('e') == -1){
                getNextChar();
                //the 'e' must be followed by a digit
                if(Character.isDigit(currChar)){
                    lexemeString += prevChar;
                }
                //otherwise we must push both characters back
                else{
                    pushBackCurrChar();
                    pushBackCurrChar();
                }

            }
            //if a '.' is found after digits, we must check to see if it
            //signifies a real number or some other lexeme following either a
            //real number or a real number

            else if(currChar == '.' && lexemeString.matches("(\\+|-)?\\d+")){
                //if the period is surrounded by two digits, the lexeme
                //is a real number
                getNextChar();
                if (Character.isDigit(currChar)){
                    lexemeString += prevChar;
                    lexemeString += currChar;
                    getNextChar();
                }
                //otherwise the period does not relate to a numerical lexeme,
                //and will be assembled as the next lexeme
                //NOTE: HOW DOES THE LEXER HANDLE SITUATIONS WHERE THE '.'
                //CHARACTER APPEARS BEFORE A BLANKSPACE? IS THIS THE APPROPRIATE
                //SPACE TO HANDLE THAT?
                else if(currChar == ' '){
                    //one solution is to make the '.' the next character and
                    //push it back to the charStream. Can't use a call to
                    //pushBackCurrChar since the current character is the
                    //blankspace
                    currChar = prevChar;
                    //under this approach we must also make the previous char
                    //a blank to ensure we don't 'double count' the '.' and 
                    //mistake the next lexeme for a doubledot
                    prevChar = ' ';
                    pushBackCurrChar();
                }
                //if both the previous and current characters are '.'
                //then we must push both characters back so we can properly
                //create the lexeme
                else if(currChar == '.' && prevChar == '.'){
                    pushBackCurrChar();
                    pushBackCurrChar();
                }
                else{
                    pushBackCurrChar();
                    break;
                }
            }
            //it's possible the '.' begins a decimal, which we must check
            else if(currChar == '.' && lexemeString.equals("")){
                getNextChar();
                if (Character.isDigit(currChar)){
                    lexemeString += prevChar;
                    lexemeString += currChar;
                }
                else{
                    pushBackCurrChar();
                }
                getNextChar();
            }
            else{
                //it's possible the character belongs to another lexeme
                //set current char to the period (the prevChar)
                //so it can be examined on the next lexeme pass
                pushBackCurrChar();
                break;
            }
        }
        //if the delimiter is significant (not a blank) it must be pushed back
        //to the charStream to be examined as the next lexeme.
        if(delimiterCheck(currChar)){
            pushBackCurrChar();
        }
        return lexemeString;
    }
}
