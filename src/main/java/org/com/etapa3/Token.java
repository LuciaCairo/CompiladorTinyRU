package org.com.etapa3;

public class Token {
    private final int line; // Numero de fila donde comienza el lexema
    private final int col; // Numero de columna donde comienza el lexema
    private final String name; // Nombre del token
    private final String lexema; // Lexema del token

    public Token(int line, int col, String name, String lexema) {
        this.line = line;
        this.col = col;
        this.name = name;
        this.lexema = lexema;
    }

    // Obtener la linea del token
    public int getLine() {
        return line;
    }

    // Obtener la columna del token
    public int getCol() {
        return col;
    }

    // Obtener el nombre del token
    public String getName() {
        return name;
    }

    // Obtener el lexema del token
    public String getLexema() {
        return lexema;
    }
}
