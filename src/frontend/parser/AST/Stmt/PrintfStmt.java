package frontend.parser.AST.Stmt;

import frontend.lexer.Token;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.Node;

import java.util.ArrayList;

public class PrintfStmt implements Stmt, BlockItem, Node {
    private Token formatString;
    private ArrayList<Exp> exps; // maybe null

    // Stmt -> 'printf' '(' StringConst { ',' Exp } ')' ';' // i j
    public PrintfStmt(Token formatString, ArrayList<Exp> exps) {
        this.formatString = formatString;
        this.exps = exps;
    }

    public Token getFormatString() {
        return formatString;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PRINTFTK printf\n");
        sb.append("LPARENT (\n");
        sb.append(formatString.toString());
        if (exps != null) {
            for (Exp exp : exps) {
                sb.append("COMMA ,\n");
                sb.append(exp.toString());
            }
        }
        sb.append("RPARENT )\n");
        sb.append("SEMICN ;\n");
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
