package utils;

import java.util.Objects;

public class Error {
    private int line;
    private char errorKey;

    public Error(int line, char errorKey) {
        this.line = line;
        this.errorKey = errorKey;
    }

    public int getLine() {
        return line;
    }

    public char getErrorKey() {
        return errorKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Error other) {
            return (this.line == other.getLine()) && (this.errorKey == other.getErrorKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, errorKey);
    }

    @Override
    public String toString() {
        return this.line + " " + errorKey + "\n";
    }
}
