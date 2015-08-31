package GrammarSymbols;

public interface GrammarSymbol
{
    boolean isToken();
    boolean isNonTerminal();
    boolean isAction();
}