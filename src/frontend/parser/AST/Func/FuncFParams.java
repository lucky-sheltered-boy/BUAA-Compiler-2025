package frontend.parser.AST.Func;

import frontend.parser.Node;

import java.util.ArrayList;

public class FuncFParams implements Node {
    private ArrayList<FuncFParam> funcFParams;

    // FuncFParams → FuncFParam {',' FuncFParam}
    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcFParams.get(0).toString());
        for (int i = 1; i < funcFParams.size(); i++) {
            sb.append("COMMA ,\n");
            sb.append(funcFParams.get(i).toString());
        }
        sb.append("<FuncFParams>\n");
        return sb.toString();
    }
}
