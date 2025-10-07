import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;
import utils.ErrorLog;
import utils.Printer;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Compiler {
    private final static String filePath = "testfile.txt";

    public static void main(String[] args) throws IOException {
        // 文件读取
        PushbackReader pushbackReader;
        try {
            pushbackReader = new PushbackReader(new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8)), 2);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("初始化 Lexer 失败: 文件未找到 " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 词法分析
        TokenStream tokenStream = Lexer.getInstance(pushbackReader).lex();
        Printer.printToLexer(tokenStream.toString());

        // 输出错误
        if (ErrorLog.getInstance().getErrorNum() != 0) {
            System.out.println("error");
            Printer.printToError(ErrorLog.getInstance().toString());
            return;
        }
    }
}
