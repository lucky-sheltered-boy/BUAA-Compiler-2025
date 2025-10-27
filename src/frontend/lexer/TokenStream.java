package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int pos = 0;
    private Token curToken = null;

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public Token next() {
        if (pos >= tokens.size()) {
            curToken = new Token(TokenType.EOF, "end", -1);
            return curToken;
        }
        // System.out.println(p + "  " + tokens.get(p).getLine() + "   " + tokens.get(p));
        curToken = tokens.get(pos++);
        return curToken;
    }

    public Token getCurToken() {
        return curToken;
    }

    public Token peek(int delta) {
        if (pos + delta - 1 >= tokens.size()) {
            return new Token(TokenType.EOF, "end", -1);
        }
        return tokens.get(pos + delta - 1);
    }

    public Token last() {
        if (pos <= 0) {
            return new Token(TokenType.EOF, "end", -1);
        }
        return tokens.get(pos - 2);
    }

    public int getCurPos() {
        return pos;
    }

    public void setCurPos(int pos) {
        this.pos = pos;
        if (pos > 0 && pos <= tokens.size()) {
            this.curToken = tokens.get(pos - 1);
        } else {
            this.curToken = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }
}
