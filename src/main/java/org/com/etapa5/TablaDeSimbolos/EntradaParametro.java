package org.com.etapa5.TablaDeSimbolos;

public class EntradaParametro {
    private String nombre;
    private String tipo;
    private int pos, line, col;

    // Constructor
    public EntradaParametro(String nombre, String tipo, int pos, int line, int col){
        this.nombre = nombre;
        this.tipo = tipo;
        this.pos = pos;
        this.line = line;
        this.col = col;
    }

    public int getPos() {
        return pos;
    }
    public String getName() {
        return nombre;
    }
    public int getLine() {
        return line;
    }
    public int getCol() {
        return col;
    }
    public String getType() {
        return tipo;
    }

    public String imprimeParametro(){
        return
                "\t\t\t\"tipo\": \""+ this.tipo +"\","+
                "\n\t\t\t\"posicion\": "+ this.pos ;
    }
}
