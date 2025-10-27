package frontend.parser.AST.Exp.PrimaryExp;

import frontend.parser.AST.Exp.Exp;
import frontend.parser.Node;

public class PEExp implements PrimaryExp, Node {
    private Exp exp;

    // PrimaryExp → '(' Exp ')'
    public PEExp(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LPARENT (\n");
        sb.append(exp.toString());
        sb.append("RPARENT )\n");
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}