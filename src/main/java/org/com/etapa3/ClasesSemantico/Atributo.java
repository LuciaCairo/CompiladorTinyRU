package org.com.etapa3.ClasesSemantico;
import java.util.Hashtable;

public class Atributo {
    private String nombre;
    private String tipo;
    private boolean isPublic = true;
    private int pos;

    // Constructor
    public Atributo(String nombre, String tipo, boolean isPublic,int pos){
        this.nombre = nombre;
        this.tipo = tipo;
        this.isPublic = isPublic;
        this.pos = pos;

    }

}
