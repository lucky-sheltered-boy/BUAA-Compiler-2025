package frontend.parser.AST.Stmt;

import frontend.parser.AST.Block;
import frontend.parser.AST.BlockItem;
import frontend.parser.Node;

public class BlockStmt implements Stmt, BlockItem, Node {
    private Block block;

    // Stmt -> Block
    public BlockStmt(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(block.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
