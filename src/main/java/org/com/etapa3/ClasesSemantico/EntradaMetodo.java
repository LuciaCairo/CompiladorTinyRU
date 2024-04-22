package org.com.etapa3.ClasesSemantico;

import java.util.Hashtable;
import java.util.List;

public class EntradaMetodo {
    private String nombre;
    private boolean isStatic = false;
    private String ret;
    private int pos;
    private Hashtable<String, EntradaParametro> parametros;

    // Constructor
    public EntradaMetodo(String nombre, boolean isStatic, int pos){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = null;
        this.pos = pos;
        this.parametros = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return nombre;
    }

    // Functions
    public String imprimeMetodo(int num){
        return "\t\t\"static\": \""+this.isStatic+"\"," +
                "\n\t\t\"retorno\": "+ this.ret +","+
                "\n\t\t\"posicion\": "+ num +
                "\n\t\t\"paramF\": ";
    }

}
