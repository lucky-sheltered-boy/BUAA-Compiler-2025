package frontend.parser.AST.CIV;

import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.Node;

import java.util.ArrayList;

public class CIVConstExps implements ConstInitVal, Node {
    ArrayList<ConstExp> constExps;

    // ConstInitVal → '{' [ ConstExp { ',' ConstExp } ] '}'
    public CIVConstExps(ArrayList<ConstExp> constExps) {
        this.constExps = constExps;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LBRACE {\n");
        boolean flag = false;
        for(ConstExp constExp : constExps) {
            if (flag) {
                sb.append("COMMA ,\n");
                sb.append(constExp.toString());
            } else {
                sb.append(constExp.toString());
                flag = true;
            }
        }
        sb.append("RBRACE }\n");
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
