package frontend.Visitor.Symbol;

public class Symbol {
    protected String value;
    protected SymbolType type;

    public Symbol() {
    }

    public String getValue() {
        return value;
    }

    public SymbolType getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }
}
