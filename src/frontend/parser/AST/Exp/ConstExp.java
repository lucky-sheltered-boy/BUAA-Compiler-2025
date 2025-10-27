package frontend.parser.AST.Exp;

import frontend.parser.Node;

public class ConstExp implements Node {
    private AddExp addExp;

    // ConstExp → AddExp
    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append("<ConstExp>\n");
        return sb.toString();
    }
}
