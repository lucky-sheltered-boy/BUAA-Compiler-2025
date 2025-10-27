package frontend.parser.AST.Exp;

import frontend.parser.Node;

import java.util.ArrayList;

public class LOrExp implements Node {
    ArrayList<LAndExp> lAndExps;

    // LOrExp → LAndExp { '||' LAndExp }
    public LOrExp(ArrayList<LAndExp> lAndExps) {
        this.lAndExps = lAndExps;
    }

    public ArrayList<LAndExp> getLAndExps() {
        return lAndExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lAndExps.get(0).toString());
        sb.append("<LOrExp>\n");
        for (int i = 1; i < lAndExps.size(); i++) {
            sb.append("OR ||\n");
            sb.append(lAndExps.get(i).toString());
            sb.append("<LOrExp>\n");
        }
        return sb.toString();
    }
}
