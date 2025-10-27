package frontend.parser.AST.Func;

import frontend.lexer.Token;
import frontend.parser.AST.Block;
import frontend.parser.Node;

public class FuncDef implements Node {
    private FuncType funcType;
    private Token ident;
    private FuncFParams funcFParams; // 可能为null
    private Block block;

    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    public FuncDef (FuncType funcType, Token ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append("LPARENT (\n");
        if (funcFParams != null) {
            sb.append(funcFParams.toString());
        }
        sb.append("RPARENT )\n");
        sb.append(block.toString());
        sb.append("<FuncDef>\n");
        return sb.toString();
    }
}
