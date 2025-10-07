package utils;

import java.util.ArrayList;
import java.util.HashSet;

public class ErrorLog {
    private static ErrorLog errorLog;
    private ArrayList<Error> errors = new ArrayList<>();

    private ErrorLog() {}

    public static ErrorLog getInstance() {
        if (null == errorLog) {
            errorLog = new ErrorLog();
        }
        return errorLog;
    }

    public void addError(Error error) {
        this.errors.add(error);
    }

    public int getErrorNum() {
        return errors.size();
    }

    public String toString() {
        HashSet<Error> uniqueErrors = new HashSet<>(errors);
        errors = new ArrayList<>(uniqueErrors);
        errors.sort((e1, e2) -> {
            if (e1.getLine() != e2.getLine()) {
                return Integer.compare(e1.getLine(), e2.getLine());
            } else {
                return Character.compare(e1.getErrorKey(), e2.getErrorKey());
            }
        });
        StringBuilder sb = new StringBuilder();
        for (Error error : errors) {
            sb.append(error.toString());
        }
        return sb.toString();
    }
}
