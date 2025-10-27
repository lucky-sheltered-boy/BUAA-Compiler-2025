package frontend.parser.AST.Exp.UnaryExp;

import frontend.lexer.Token;
import frontend.parser.Node;

public class UnaryOp implements Node {
    private Token op;

    // UnaryOp → '+' | '−' | '!'
    public UnaryOp(Token op) {
        this.op = op;
    }

    public Token getOp() {
        return op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(op.toString());
        sb.append("<UnaryOp>\n");
        return sb.toString();
    }
}
