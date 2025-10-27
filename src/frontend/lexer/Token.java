package frontend.lexer;

import frontend.parser.Node;

public class Token implements Node {
    private TokenType type;
    private String value;
    private int line;

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public int getLine() {
        return this.line;
    }

    public boolean is(TokenType type) {
        return this.type == type;
    }

    public String toString() {
        return String.format("%s %s\n", type, value);
    }
}