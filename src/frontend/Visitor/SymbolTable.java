package frontend.Visitor;

import frontend.Visitor.Symbol.Symbol;
import frontend.lexer.Token;

import java.util.ArrayList;

public class SymbolTable {
    private Integer id;
    private SymbolTable father;
    private ArrayList<SymbolTable> children;
    private ArrayList<Symbol> symbols;

    public SymbolTable(Integer id, SymbolTable father) {
        this.id = id;
        this.father = father;
        this.children = new ArrayList<>();
        this.symbols = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public SymbolTable getFather() {
        return father;
    }

    public ArrayList<SymbolTable> getChildren() {
        return children;
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    public void addChild(SymbolTable child) {
        this.children.add(child);
    }

    public void addSymbol(Symbol symbol) {
        this.symbols.add(symbol);
    }

    public boolean has(Token ident) {  // 当前符号表内是否含有该符号
        String index = ident.getValue();
        for (Symbol o : symbols) {
            if (o.getValue().equals(index)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDefined(Token ident) {  // 当前符号是否被定义过
        if (this.has(ident)) {
            return true;
        }
        if (this.father != null) {
            return this.father.isDefined(ident);
        } else {
            return false;
        }
    }

    public Symbol find(Token ident) {
        String value = ident.getValue();
        for (Symbol symbol: symbols) {
            if (symbol.getValue().equals(value)) {
                return symbol;
            }
        }
        if (this.father != null) {
            return this.father.find(ident);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbols) {
            sb.append(this.id).append(" ");
            sb.append(symbol.toString());
        }
        for (SymbolTable symbolTable : children) {
            sb.append(symbolTable.toString());
        }
        return sb.toString();
    }
}
