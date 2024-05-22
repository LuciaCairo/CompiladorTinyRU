package org.com.etapa4;

public class LexicalErrorException extends IllegalArgumentException {
    private int lineNumber;
    private int columnNumber;
    private String description;

    public LexicalErrorException(int lineNumber, int columnNumber, String description) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.description = description;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getDescription() {
        return description;
    }
}

