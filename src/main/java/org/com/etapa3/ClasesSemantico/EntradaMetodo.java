package org.com.etapa3.ClasesSemantico;

import java.util.List;

public class EntradaMetodo {
    private String nombre;
    private boolean isStatic = false;
    private String ret;
    private int pos;
    private List<EntradaParametro> entradaParametros;

    // Constructor
    public EntradaMetodo(String nombre, boolean isStatic, String ret, int pos, List<EntradaParametro> entradaParametros){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = ret;
        this.pos = pos;
        this.entradaParametros = entradaParametros;
    }

}
