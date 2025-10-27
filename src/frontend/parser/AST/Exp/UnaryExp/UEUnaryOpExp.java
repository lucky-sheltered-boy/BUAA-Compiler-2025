package frontend.parser.AST.Exp.UnaryExp;

import frontend.parser.Node;

public class UEUnaryOpExp implements UnaryExp, Node {
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    // UnaryExp → UnaryOp UnaryExp
    public UEUnaryOpExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(unaryOp.toString());
        sb.append(unaryExp.toString());
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
