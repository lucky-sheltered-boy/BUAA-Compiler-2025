package frontend.parser.AST;

import frontend.lexer.Token;
import frontend.parser.AST.CIV.ConstInitVal;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.Node;

public class ConstDef implements Node {
    private Token ident; // 标识符
    private ConstExp constExp; // 每一维的表达式列表，可能为 null
    private ConstInitVal constInitVal; // 常量初值

    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    public ConstDef(Token ident, ConstExp constExp, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.constInitVal = constInitVal;
    }

    public Token getIdent() {
        return ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (constExp != null) {
            sb.append("LBRACK [\n");
            sb.append(constExp.toString());
            sb.append("RBRACK ]\n");
        }
        sb.append("ASSIGN =\n");
        sb.append(constInitVal.toString());
        sb.append("<ConstDef>\n");
        return sb.toString();
    }
}
