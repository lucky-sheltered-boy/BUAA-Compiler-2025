package frontend.parser.AST;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AST.IV.InitVal;
import frontend.parser.Node;

public class VarDef implements Node {
    private Token Ident;
    private ConstExp constExp; // maybe null
    private InitVal initVal; // maybe null

    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    public VarDef(Token ident, ConstExp constExp, InitVal initVal) {
        Ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
    }

    public Token getIdent() {
        return Ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public InitVal getInitVal() {
        return initVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Ident.toString());
        if (constExp != null) {
            sb.append("LBRACK [\n");
            sb.append(constExp.toString());
            sb.append("RBRACK ]\n");
        }
        if (initVal != null) {
            sb.append("ASSIGN =\n");
            sb.append(initVal.toString());
        }
        sb.append("<VarDef>\n");
        return sb.toString();
    }
}
