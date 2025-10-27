package frontend.parser.AST.Exp;

import frontend.parser.Node;

import java.util.ArrayList;

public class RelExp implements Node {
    private ArrayList<Node> nodes;

    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    // RelExp -> AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    public RelExp(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nodes.get(0).toString());
        sb.append("<RelExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            sb.append(nodes.get(i).toString());
            sb.append(nodes.get(i + 1).toString());
            sb.append("<RelExp>\n");
        }
        return sb.toString();
    }
}
