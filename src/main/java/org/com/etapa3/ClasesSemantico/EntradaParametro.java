package org.com.etapa3.ClasesSemantico;

public class EntradaParametro {
    private String nombre;
    private String tipo;
    private int pos;

    // Constructor
    public EntradaParametro(String nombre, String tipo, int pos){
        this.nombre = nombre;
        this.tipo = tipo;
        this.pos = pos;
    }
    public String imprimeParametro(){
        return
                "\t\t\t\"tipo\": \""+ this.tipo +"\","+
                "\n\t\t\t\"posicion\": "+ this.pos ;
    }
}
