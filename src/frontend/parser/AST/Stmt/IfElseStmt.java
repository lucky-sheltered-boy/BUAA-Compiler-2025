package frontend.parser.AST.Stmt;

import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Cond;
import frontend.parser.Node;

public class IfElseStmt implements Stmt, BlockItem, Node {
    private Cond cond;
    private Stmt ifStmt;
    private Stmt elseStmt;  // maybe null

    // Stmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    public IfElseStmt(Cond cond, Stmt ifStmt, Stmt elseStmt) {
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getIfStmt() {
        return ifStmt;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IFTK if\n");
        sb.append("LPARENT (\n");
        sb.append(cond.toString());
        sb.append("RPARENT )\n");
        sb.append(ifStmt.toString());
        if (elseStmt != null) {
            sb.append("ELSETK else\n");
            sb.append(elseStmt.toString());
        }
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
