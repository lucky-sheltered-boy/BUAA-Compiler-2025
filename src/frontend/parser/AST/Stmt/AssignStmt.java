package frontend.parser.AST.Stmt;

import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AST.Exp.LVal;
import frontend.parser.Node;

public class AssignStmt implements Stmt, BlockItem, Node {
    private LVal lVal;
    private Exp exp;

    // Stmt -> LVal '=' Exp ';'
    public AssignStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public LVal getLVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append("ASSIGN =\n");
        sb.append(exp.toString());
        sb.append("SEMICN ;\n");
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
