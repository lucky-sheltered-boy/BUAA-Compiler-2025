package frontend.parser.AST.Stmt;

import frontend.lexer.Token;
import frontend.parser.AST.BlockItem;
import frontend.parser.Node;

public class BreakStmt implements Stmt, BlockItem, Node {
    private Token breaktk;

    // Stmt → 'break' ';'
    public BreakStmt(Token breaktk) {
        this.breaktk = breaktk;
    }

    public Token getBreaktk() {
        return breaktk;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(breaktk.toString());
        sb.append("SEMICN ;\n");
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
