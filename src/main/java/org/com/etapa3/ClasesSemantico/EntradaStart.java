package org.com.etapa3.ClasesSemantico;

import java.util.Hashtable;

public class EntradaStart {
    private String name;
    private String ret;
    private int pos;
    private Hashtable<String,EntradaAtributo> atributos;

    // Constructor
    public EntradaStart(){
        this.name= "start";
        this.ret = "void";
        this.pos = 0;
    }
}
