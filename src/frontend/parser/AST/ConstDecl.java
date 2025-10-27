package frontend.parser.AST;

import frontend.lexer.Token;
import frontend.parser.Node;

import java.util.ArrayList;

public class ConstDecl implements Decl, BlockItem, Node {
    private Token Const; // const
    private Token BType; // int
    private ArrayList<ConstDef> constDefs;

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    public ConstDecl(Token Const, Token BType, ArrayList<ConstDef> constDefs) {
        this.Const = Const;
        this.BType = BType;
        this.constDefs = constDefs;
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Const.toString());
        sb.append(BType.toString());
        boolean flag = false;
        for (ConstDef constDef : constDefs) {
            if (flag) {
                sb.append("COMMA ,\n");
                sb.append(constDef.toString());
            } else {
                sb.append(constDef.toString());
                flag = true;
            }
        }
        sb.append("SEMICN ;\n");
        sb.append("<ConstDecl>\n");
        return sb.toString();
    }
}
