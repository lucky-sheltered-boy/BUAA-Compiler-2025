package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.Node;

import java.util.ArrayList;

public class AddExp implements Node {
    private ArrayList<Node> nodes;

    // AddExp → MulExp { ('+' | '−') MulExp }
    public AddExp(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public Token tryGetArrayIdent() {
        if (nodes.size() != 1) {
            return null;
        }
        return ((MulExp) nodes.get(0)).tryGetArrayIdent();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nodes.get(0).toString());
        sb.append("<AddExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            sb.append(nodes.get(i).toString());
            sb.append(nodes.get(i + 1).toString());
            sb.append("<AddExp>\n");
        }
        return sb.toString();
    }
}
