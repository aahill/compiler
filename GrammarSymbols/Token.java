package GrammarSymbols;

public class Token {
    public TokenType type;
    public Object value;
    /**
     * general constructor for a token
     * @param type the string value of the token
     * @param value they type
     */
    public Token(TokenType type, String value){
        this.type = type;
        this.value = value;
    }
    @Override public String toString(){
        return "Value: \""+value+"\" Type: "+type;
    }
    //sets the lexeme of the token
    public void setVal(String lexeme){
        this.value = lexeme;
    }
    public String getVal(){
        return (String)this.value;
    }

    public TokenType getType(){
        return this.type;
    }

    public void setType(TokenType newType){
        this.type = newType;
    }

    //method to reclassify the value of the token
    public void reclassify(){
        //switch statement for keywords and symbols
        switch(((String)this.value).toLowerCase()){
            case "array":
                this.setType(TokenType.ARRAY);
                break;
            case "begin":
                this.setType(TokenType.BEGIN);
                break;
            case "do":
                this.setType(TokenType.DO);
                break;
            case "else":
                this.setType(TokenType.ELSE);
                break;
            case "end":
                this.setType(TokenType.END);
                break;
            case "function":
                this.setType(TokenType.FUNCTION);
                break;
            case "if":
                this.setType(TokenType.IF);
                break;
            case "integer":
                this.setType(TokenType.INTEGER);
                break;
            case "not":
                this.setType(TokenType.NOT);
                break;
            case "of":
                this.setType(TokenType.OF);
                break;
            case "or":
                this.setType(TokenType.ADDOP);
                break;
            case "procedure":
                this.setType(TokenType.PROCEDURE);
                break;
            case "program":
                this.setType(TokenType.PROGRAM);
                break;
            case "real":
                this.setType(TokenType.REAL);
                break;
            case "result":
                this.setType(TokenType.RESULT);
                break;
            case "then":
                this.setType(TokenType.THEN);
                break;
            case "var":
                this.setType(TokenType.VAR);
                break;
            case "while":
                this.setType(TokenType.WHILE);
                break;
            case "div":
                this.setType(TokenType.MULOP);
                break;
            case "and":
                this.setType(TokenType.MULOP);
                break;
            case "mod":
                this.setType(TokenType.MULOP);
                break;
            case ".":
                this.setType(TokenType.ENDMARKER);
                break;
            case "..":
                this.setType(TokenType.DOUBLEDOT);
                break;
            case "(":
                this.setType(TokenType.LEFTPAREN);
                break;
            case ";":
                this.setType(TokenType.SEMICOLON);
                break;
            case "=":
                this.setType(TokenType.RELOP);
                break;
            case ")":
                this.setType(TokenType.RIGHTPAREN);
                break;
            case "]":
                this.setType(TokenType.RIGHTBRACKET);
                break;
            case "[":
                this.setType(TokenType.LEFTBRACKET);
                break;
            case ":":
                this.setType(TokenType.COLON);
                break;
            case "+":
                this.setType(TokenType.ADDOP);
                break;
            case "-":
                this.setType(TokenType.ADDOP);
                break;
            case "*":
                this.setType(TokenType.MULOP);
                break;
            case ",":
                this.setType(TokenType.COMMA);
                break;
            case "/":
                this.setType(TokenType.MULOP);
                break;
            case ">":
                this.setType(TokenType.RELOP);
                break;
            case "<":
                this.setType(TokenType.RELOP);
                break;
            case "<=":
                this.setType(TokenType.RELOP);
                break;
            case ">=":
                this.setType(TokenType.RELOP);
                break;
            case "<>":
                this.setType(TokenType.RELOP);
                break;
            case ":=":
                this.setType(TokenType.ASSIGNOP);
                break;
            case "eof":
                this.setType(TokenType.ENDOFFILE);
                break;
        }
        //if the token isn't one of the above keywords/symbols, then
        //its type will still be null, and will be reclassified
        if(this.type == TokenType.NULL){
            //determines if the value is an integer constant
            if(((String)this.value).matches("(\\+|-)?\\d+")){
                this.setType(TokenType.INTCONSTANT);
                this.value = Integer.parseInt((String)this.value);
            }
            //determines if the value is a real constant
            else if(((String)this.value).matches("(\\+|-)?\\d+\\.\\d+|(\\+|-)?\\d+.?\\d+e\\d+")){
                this.setType(TokenType.REALCONSTANT);
                this.value = Float.parseFloat((String)this.value);
            }
            else{
                this.setType(TokenType.IDENTIFIER);
            }
        }
    }
}
