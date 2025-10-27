package frontend.parser.AST.Func;

import frontend.lexer.Token;
import frontend.parser.Node;

public class FuncType implements Node {
    private Token funcType; // 'int' | 'void'

    public FuncType(Token funcType) {
        this.funcType = funcType;
    }

    public Token getFuncType() {
        return funcType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append("<FuncType>\n");
        return sb.toString();
    }
}
