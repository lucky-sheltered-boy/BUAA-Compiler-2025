package frontend.parser.AST.CIV;

import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.Node;

public class CIVConstExp implements ConstInitVal, Node {
    private ConstExp constExp;

    // ConstInitVal → ConstExp
    public CIVConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }

    public ConstExp getConstExp() {
        return this.constExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constExp.toString());
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
