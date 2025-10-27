package frontend.parser.AST.IV;

import frontend.parser.AST.Exp.Exp;
import frontend.parser.Node;

import java.util.ArrayList;

public class IVExps implements InitVal, Node {
    ArrayList<Exp> exps;

    // InitVal → '{' [ Exp { ',' Exp } ] '}'
    public IVExps(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LBRACE {\n");
        boolean flag = false;
        for (Exp exp : exps) {
            if (flag) {
                sb.append("COMMA ,\n");
                sb.append(exp.toString());
            } else {
                sb.append(exp.toString());
                flag = true;
            }
        }
        sb.append("RBRACE }\n");
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
