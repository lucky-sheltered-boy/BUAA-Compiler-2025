package frontend.parser.AST;

import frontend.lexer.Token;
import frontend.parser.Node;

import java.util.ArrayList;

public class VarDecl implements Decl, BlockItem, Node {
    private Token Static; // maybe null
    private Token BType; // int
    private ArrayList<VarDef> varDefs;

    // VarDecl → ['static'] BType VarDef { ',' VarDef } ';'
    public VarDecl(Token Static, Token BType, ArrayList<VarDef> varDefs) {
        this.Static = Static;
        this.BType = BType;
        this.varDefs = varDefs;
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }

    public boolean isStatic() {
        return Static != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (Static != null) {
            sb.append(Static.toString());
        }
        sb.append(BType.toString());
        boolean flag = false;
        for (VarDef varDef : varDefs) {
            if (flag) {
                sb.append("COMMA ,\n");
                sb.append(varDef.toString());
            } else {
                sb.append(varDef.toString());
                flag = true;
            }
        }
        sb.append("SEMICN ;\n");
        sb.append("<VarDecl>\n");
        return sb.toString();
    }
}
