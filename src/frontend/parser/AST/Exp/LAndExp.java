package frontend.parser.AST.Exp;

import frontend.parser.Node;

import java.util.ArrayList;

public class LAndExp implements Node {
    private ArrayList<EqExp> eqExps;

    // LAndExp → EqExp | LAndExp '&&' EqExp
    // LAndExp -> EqExp { '&&' EqExp }
    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(eqExps.get(0).toString());
        sb.append("<LAndExp>\n");
        for (int i = 1; i < eqExps.size(); i++) {
            sb.append("AND &&\n");
            sb.append(eqExps.get(i).toString());
            sb.append("<LAndExp>\n");
        }
        return sb.toString();
    }
}
