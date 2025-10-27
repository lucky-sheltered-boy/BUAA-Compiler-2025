package frontend.parser.AST.Exp;

import frontend.parser.Node;

public class Cond implements Node {
    private LOrExp lOrExp;

    // Cond → LOrExp
    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp.toString());
        sb.append("<Cond>\n");
        return sb.toString();
    }
}
