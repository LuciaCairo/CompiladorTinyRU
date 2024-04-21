package org.com.etapa3.ClasesSemantico;

import java.util.List;
import java.util.Hashtable;

public class Metodo {
    private String nombre;
    private boolean isStatic = false;
    private String ret;
    private int pos;
    private List<Parametro> parametros;

    // Constructor
    public Metodo(String nombre, boolean isStatic, String ret, int pos, List<Parametro> parametros){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = ret;
        this.pos = pos;
        this.parametros = parametros;
    }

}
