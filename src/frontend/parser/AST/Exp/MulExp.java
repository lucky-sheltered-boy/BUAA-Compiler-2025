package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.UnaryExp.UEPrimaryExp;
import frontend.parser.AST.Exp.UnaryExp.UnaryExp;
import frontend.parser.Node;

import java.util.ArrayList;

public class MulExp implements Node {
    private ArrayList<Node> nodes;

    // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    public MulExp(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public Token tryGetArrayIdent() {
        if (nodes.size() != 1) {
            return null;
        }
        if (nodes.get(0) instanceof UEPrimaryExp) {
            return ((UEPrimaryExp) nodes.get(0)).tryGetArrayIdent();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nodes.get(0).toString());
        sb.append("<MulExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            sb.append(nodes.get(i).toString());
            sb.append(nodes.get(i + 1).toString());
            sb.append("<MulExp>\n");
        }
        return sb.toString();
    }
}
