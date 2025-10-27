package frontend.parser.AST.Func;

import frontend.parser.AST.Block;
import frontend.parser.Node;

public class MainFuncDef implements Node {
    private Block block;

    // MainFuncDef → 'int' 'main' '(' ')' Block
    public MainFuncDef(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INTTK int\n");
        sb.append("MAINTK main\n");
        sb.append("LPARENT (\n");
        sb.append("RPARENT )\n");
        sb.append(block.toString());
        sb.append("<MainFuncDef>\n");
        return sb.toString();
    }
}
