package org.com.etapa5.TablaDeSimbolos;

public class EntradaVariable {
    private String name;
    private String type;
    private int pos = 0;
    private int col, line;

    // Constructor
    public EntradaVariable(String name, String type, int pos, int line, int col){
        this.name = name;
        this.type = type;
        this.pos = pos;
        this.line = line;
        this.col = col;
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public int getPos() {
        return pos;
    }
    public int getLine() {
        return line;
    }
    public int getCol() {
        return col;
    }

    // Setters



    // Functions
    public String imprimeVar(){
        return "\t\t\"tipo\": \""+this.type+"\"," +
                "\n\t\t\"posicion\": "+ this.pos ;
    }

    public String imprimeVarMet(){
        return "\t\t\t\"tipo\": \""+this.type+"\"," +
                "\n\t\t\t\"posicion\": "+ this.pos ;
    }

}
