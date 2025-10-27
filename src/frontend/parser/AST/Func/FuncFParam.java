package frontend.parser.AST.Func;

import frontend.lexer.Token;
import frontend.parser.Node;

public class FuncFParam implements Node {
    private Token BType; // int
    private Token Ident; // 参数名
    private boolean isArray; // 是否为数组参数

    // FuncFParam → BType Ident ['[' ']']
    public FuncFParam(Token BType, Token Ident, boolean isArray) {
        this.BType = BType;
        this.Ident = Ident;
        this.isArray = isArray;
    }

    public Token getBType() {
        return BType;
    }

    public Token getIdent() {
        return Ident;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BType.toString());
        sb.append(Ident.toString());
        if (isArray) {
            sb.append("LBRACK [\n");
            sb.append("RBRACK ]\n");
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
