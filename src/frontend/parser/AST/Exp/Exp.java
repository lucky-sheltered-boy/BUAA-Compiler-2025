package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.Node;

public class Exp implements Node {
    private AddExp addExp;

    // Exp → AddExp
    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public Token tryGetArrayIdent() {
        return addExp.tryGetArrayIdent();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append("<Exp>\n");
        return sb.toString();
    }
}
