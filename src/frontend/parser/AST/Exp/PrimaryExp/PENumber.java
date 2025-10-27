package frontend.parser.AST.Exp.PrimaryExp;

import frontend.lexer.Token;
import frontend.parser.Node;

public class PENumber implements PrimaryExp, Node {
    private NumberExp number;

    // PrimaryExp → Number
    public PENumber (NumberExp number) {
        this.number = number;
    }

    public NumberExp getNumber() {
        return number;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(number.toString());
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
