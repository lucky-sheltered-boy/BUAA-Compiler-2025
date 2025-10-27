package frontend.parser.AST.Exp.PrimaryExp;

import frontend.lexer.Token;
import frontend.parser.Node;

public class NumberExp implements Node {
    private Token number;

    // Number -> IntConst
    public NumberExp(Token number) {
        this.number = number;
    }

    public Token getNumber() {
        return number;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(number.toString());
        sb.append("<Number>\n");
        return sb.toString();
    }
}
