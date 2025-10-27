package frontend.parser.AST.Stmt;

import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.Node;

public class ExpStmt implements Stmt, BlockItem, Node {
    private final Exp exp;

    // Stmt -> [Exp] ';'
    public ExpStmt(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append(exp.toString());
        }
        sb.append("SEMICN ;\n");
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
