package frontend.parser.AST;

import frontend.lexer.Token;
import frontend.parser.Node;

import java.util.ArrayList;

public class Block implements Node {
    private ArrayList<BlockItem> blockItems;
    private Token rightBrace;

    // Block → '{' { BlockItem } '}'
    public Block(ArrayList<BlockItem> blockItems, Token rightBrace) {
        this.blockItems = blockItems;
        this.rightBrace = rightBrace;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    public Token getRightBrace() {
        return rightBrace;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LBRACE {\n");
        for (BlockItem item : blockItems) {
            sb.append(item.toString());
        }
        sb.append("RBRACE }\n");
        sb.append("<Block>\n");
        return sb.toString();
    }
}
