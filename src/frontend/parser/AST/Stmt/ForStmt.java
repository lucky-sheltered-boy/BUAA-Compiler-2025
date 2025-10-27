package frontend.parser.AST.Stmt;

import frontend.parser.Node;

import java.util.ArrayList;

public class ForStmt implements Node {
    private ArrayList<ForInitStmt> forInitStmts;

    // ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    // ForStmt -> ForInitStmt { ',' ForInitStmt }
    public ForStmt(ArrayList<ForInitStmt> forInitStmts) {
        this.forInitStmts = forInitStmts;
    }

    public ArrayList<ForInitStmt> getForInitStmts() {
        return this.forInitStmts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ForInitStmt forInitStmt : forInitStmts) {
            if (!first) {
                sb.append("COMMA ,\n");
            } else {
                first = false;
            }
            sb.append(forInitStmt.toString());
        }
        sb.append("<ForStmt>\n");
        return sb.toString();
    }
}
