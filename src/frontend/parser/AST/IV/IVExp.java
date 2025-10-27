package frontend.parser.AST.IV;

import frontend.parser.AST.Exp.Exp;
import frontend.parser.Node;

public class IVExp implements InitVal, Node {
    private Exp exp;

    // InitVal → Exp
    public IVExp(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(exp.toString());
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
