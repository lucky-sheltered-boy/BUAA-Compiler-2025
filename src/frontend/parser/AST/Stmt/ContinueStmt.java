package frontend.parser.AST.Stmt;

import frontend.lexer.Token;
import frontend.parser.AST.BlockItem;
import frontend.parser.Node;

public class ContinueStmt implements Stmt, BlockItem, Node {
    private Token continuetk;

    // Stmt -> 'continue' ';'
    public ContinueStmt(Token continuetk) {
        this.continuetk = continuetk;
    }

    public Token getContinuetk() {
        return continuetk;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(continuetk.toString());
        sb.append("SEMICN ;\n");
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
