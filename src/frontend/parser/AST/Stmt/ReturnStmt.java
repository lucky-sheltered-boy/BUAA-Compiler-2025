package frontend.parser.AST.Stmt;

import frontend.lexer.Token;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.Node;

public class ReturnStmt implements Stmt, BlockItem, Node {
    private Token returnToken;
    private Exp exp; // 可以为空

    // Stmt -> 'return' [Exp] ';'
    public ReturnStmt(Token returnToken, Exp exp) {
        this.returnToken = returnToken;
        this.exp = exp;
    }

    public Token getReturntk() {
        return returnToken;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RETURNTK return\n");
        if (exp != null) {
            sb.append(exp.toString());
        }
        sb.append("SEMICN ;\n");
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
