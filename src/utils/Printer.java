package utils;

import java.io.FileWriter;

public class Printer {
    private final static String lexerOutputPath = "lexer.txt";
    private final static String errorOutputPath = "error.txt";
    private final static String parserOutputPath = "parser.txt";
    private final static String symbolOutputPath = "symbol.txt";

    public static void printToFile(String filePath, String content) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static void printToLexer(String content) {
        printToFile(lexerOutputPath, content);
    }

    public static void printToError(String content) {
        printToFile(errorOutputPath, content);
    }

    public static void printToParser(String content) {
        printToFile(parserOutputPath, content);
    }

    public static void printToSymbol(String content) {
        printToFile(symbolOutputPath, content);
    }
}
