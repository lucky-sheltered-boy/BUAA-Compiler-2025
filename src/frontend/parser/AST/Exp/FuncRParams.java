package frontend.parser.AST.Exp;

import frontend.parser.Node;

import java.util.ArrayList;

public class FuncRParams implements Node {
    private ArrayList<Exp> exps;

    // FuncRParams → Exp {',' Exp}
    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < exps.size(); i++) {
            sb.append(exps.get(i).toString());
            if (i != exps.size() - 1) {
                sb.append("COMMA ,\n");
            }
        }
        sb.append("<FuncRParams>\n");
        return sb.toString();
    }
}
