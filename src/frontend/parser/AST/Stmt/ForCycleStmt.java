package frontend.parser.AST.Stmt;

import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Cond;
import frontend.parser.Node;

public class ForCycleStmt implements Stmt, BlockItem, Node {
    private ForStmt init; // for循环的初始化语句，可以为空
    private Cond cond; // for循环的条件表达式，可以为空
    private ForStmt step; // for循环的步进语句，可以为空
    private Stmt body; // for循环的循环体

    // Stmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    public ForCycleStmt(ForStmt init, Cond cond, ForStmt step, Stmt body) {
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.body = body;
    }

    public ForStmt getInitStmt() {
        return init;
    }

    public Cond getCond() {
        return cond;
    }

    public ForStmt getStepStmt() {
        return step;
    }

    public Stmt getBodyStmt() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FORTK for\n");
        sb.append("LPARENT (\n");
        if (init != null) {
            sb.append(init.toString());
        }
        sb.append("SEMICN ;\n");
        if (cond != null) {
            sb.append(cond.toString());
        }
        sb.append("SEMICN ;\n");
        if (step != null) {
            sb.append(step.toString());
        }
        sb.append("RPARENT )\n");
        sb.append(body.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
