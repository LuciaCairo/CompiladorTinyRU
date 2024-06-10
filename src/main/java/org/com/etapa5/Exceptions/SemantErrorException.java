package org.com.etapa5.Exceptions;

public class SemantErrorException extends IllegalArgumentException {
    private int lineNumber;
    private int columnNumber;
    private String description;
    private String functionName;

    public SemantErrorException(int lineNumber, int columnNumber, String description, String functionName) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.description = description;
        this.functionName = functionName;
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

    public String functionName() { return functionName;}
}

