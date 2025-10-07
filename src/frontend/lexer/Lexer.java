package frontend.lexer;

import utils.ErrorLog;
import utils.Error;
import java.io.*;

public class Lexer {
    private static Lexer lexer;
    private TokenStream tokenStream;
    private final PushbackReader reader;
    private int line = 1;
    private static final char EOF = (char) -1;
    private char currentChar;

    private Lexer(PushbackReader reader) {
        this.reader = reader;
        this.tokenStream = new TokenStream();
    }

    public static Lexer getInstance(PushbackReader reader) {
        if (lexer == null) {
            lexer = new Lexer(reader);
        }
        return lexer;
    }

    public TokenStream lex() throws IOException {
        read();
        while (true) {
            skipWhitespaceAndComments();    // 跳过空白符和注释
            if (isEOF()) {
                break;
            }
            Token token = getNextToken();   // 获取下一个 Token
            tokenStream.addToken(token);
        }
        return tokenStream;
    }

    public Token getNextToken() throws IOException {
        if (isSingleToken()) {
            // 单字符就可以判定为 Token
            return tackleSingleToken();
        } else if (isOneOrTwoToken()) {
            // 需要再向后读一个字符来判定的 Token
            return tackleOneOrTwoToken();
        } else if (currentChar == '\"') {
            // 字符串字面量
            return tackleStringLiteral();
        } else if (Character.isDigit(currentChar)) {
            // 数字字面量
            return tackleDigit();
        } else {
            // 标识符或关键字
            return tackleIdent();
        }
    }

    public Token tackleIdent() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            sb.append(currentChar);
            try {
                read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String ident = sb.toString();
        switch (ident) {
            case "const":
                return new Token(TokenType.CONSTTK, ident, line);
            case "int":
                return new Token(TokenType.INTTK, ident, line);
            case "static":
                return new Token(TokenType.STATICTK, ident, line);
            case "break":
                return new Token(TokenType.BREAKTK, ident, line);
            case "continue":
                return new Token(TokenType.CONTINUETK, ident, line);
            case "if":
                return new Token(TokenType.IFTK, ident, line);
            case "else":
                return new Token(TokenType.ELSETK, ident, line);
            case "main":
                return new Token(TokenType.MAINTK, ident, line);
            case "for":
                return new Token(TokenType.FORTK, ident, line);
            case "void":
                return new Token(TokenType.VOIDTK, ident, line);
            case "printf":
                return new Token(TokenType.PRINTFTK, ident, line);
            case "return":
                return new Token(TokenType.RETURNTK, ident, line);
            default:
                return new Token(TokenType.IDENFR, ident, line);
        }
    }

    public Token tackleDigit() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            sb.append(currentChar);
            read();
        }
        return new Token(TokenType.INTCON, sb.toString(), line);
    }

    public Token tackleStringLiteral() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append('\"');
        read(); // consume the opening quote
        while (currentChar != '\"' && !isEOF()) {
            sb.append(currentChar);
            read();
        }
        if (currentChar == '\"') {
            sb.append('\"');
            read(); // consume the closing quote
            return new Token(TokenType.STRCON, sb.toString(), line);
        } else {
            // Unterminated string literal
            return new Token(TokenType.STRCON, sb.toString(), line);
        }
    }

    public boolean isOneOrTwoToken() {
        return currentChar == '=' || currentChar == '!' || currentChar == '<' || currentChar == '>' ||
                currentChar == '&' || currentChar == '|';
    }

    public Token tackleOneOrTwoToken() {
        switch (currentChar) {
            case '=': {
                try {
                    if (peek() == '=') {
                        read();
                        read();
                        return new Token(TokenType.EQL, "==", line);
                    } else {
                        read();
                        return new Token(TokenType.ASSIGN, "=", line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case '!': {
                try {
                    if (peek() == '=') {
                        read();
                        read();
                        return new Token(TokenType.NEQ, "!=", line);
                    } else {
                        read();
                        return new Token(TokenType.NOT, "!", line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case '<': {
                try {
                    if (peek() == '=') {
                        read();
                        read();
                        return new Token(TokenType.LEQ, "<=", line);
                    } else {
                        read();
                        return new Token(TokenType.LSS, "<", line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case '>': {
                try {
                    if (peek() == '=') {
                        read();
                        read();
                        return new Token(TokenType.GEQ, ">=", line);
                    } else {
                        read();
                        return new Token(TokenType.GRE, ">", line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case '&': {
                try {
                    if (peek() == '&') {
                        read();
                        read();
                        return new Token(TokenType.AND, "&&", line);
                    } else {
                        // Invalid token
                        read();
                        ErrorLog.getInstance().addError(new Error(line, 'a'));
                        return new Token(TokenType.AND, "&&", line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case '|': {
                try {
                    if (peek() == '|') {
                        read();
                        read();
                        return new Token(TokenType.OR, "||", line);
                    } else {
                        // Invalid token
                        read();
                        ErrorLog.getInstance().addError(new Error(line, 'a'));
                        return new Token(TokenType.OR, "||", line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default: {
                return null; // Should not reach here
            }
        }
    }

    public boolean isSingleToken() {
        return currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' ||
                currentChar == '[' || currentChar == ']' || currentChar == ';' || currentChar == ',' ||
                currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' ||
                currentChar == '%';
    }

    public Token tackleSingleToken() throws IOException {
        switch (currentChar) {
            case '(': {
                read();
                return new Token(TokenType.LPARENT, "(", line);
            }
            case ')': {
                read();
                return new Token(TokenType.RPARENT, ")", line);
            }
            case '{': {
                read();
                return new Token(TokenType.LBRACE, "{", line);
            }
            case '}': {
                read();
                return new Token(TokenType.RBRACE, "}", line);
            }
            case '[': {
                read();
                return new Token(TokenType.LBRACK, "[", line);
            }
            case ']': {
                read();
                return new Token(TokenType.RBRACK, "]", line);
            }
            case ';': {
                read();
                return new Token(TokenType.SEMICN, ";", line);
            }
            case ',': {
                read();
                return new Token(TokenType.COMMA, ",", line);
            }
            case '+': {
                read();
                return new Token(TokenType.PLUS, "+", line);
            }
            case '-': {
                read();
                return new Token(TokenType.MINU, "-", line);
            }
            case '*': {
                read();
                return new Token(TokenType.MULT, "*", line);
            }
            case '/': {
                read();
                return new Token(TokenType.DIV, "/", line);
            }
            case '%': {
                read();
                return new Token(TokenType.MOD, "%", line);
            }
            default: {
                return null; // Should not reach here
            }
        }
    }

    public void skipWhitespaceAndComments() throws IOException {
        while (Character.isWhitespace(currentChar) || currentChar == '/') {
            if (currentChar == '\n') {
                line++;
                read();
            } else if (Character.isWhitespace(currentChar)) {
                read();
            } else if (currentChar == '/') {
                try {
                    char nextChar = peek();
                    if (nextChar == '/') {
                        // Single-line comment
                        while (currentChar != '\n' && !isEOF()) {
                            read();
                        }
                        if (currentChar == '\n') {
                            line++;
                            read();
                        }
                    } else if (nextChar == '*') {
                        // Multi-line comment
                        read(); // consume '*'
                        read(); // consume first char inside comment
                        while (true) {
                            if (isEOF()) {
                                break;  // Unterminated comment
                            }
                            if (currentChar == '*' && peek() == '/') {
                                read(); // consume '*'
                                read(); // consume '/'
                                break;
                            }
                            if (currentChar == '\n') {
                                line++;
                            }
                            read();
                        }
                    } else {
                        // Not a comment, just a '/'
                        return;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public char read() throws IOException {
        int t = reader.read();
        currentChar = (char) t;
        return currentChar;
    }

    public void unread() {
        try {
            reader.unread(currentChar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public char peek() throws IOException {
        int t = reader.read();
        char c = (char) t;
        reader.unread(c);
        return c;
    }

    public boolean isEOF() {
        return currentChar == EOF;
    }
}