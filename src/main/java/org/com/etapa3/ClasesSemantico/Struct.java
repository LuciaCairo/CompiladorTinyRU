package org.com.etapa3.ClasesSemantico;

import java.util.List;
import java.util.Hashtable;

public class Struct {
    private String nombre;
    private String herencia;
    private List<Atributo> atributos;
    private List<Metodo> metodos;

    // Constructor
    public Struct(String nombre, String herencia, List<Atributo> atributos, List<Metodo> metodos){
        this.nombre = nombre;
        this.herencia = herencia;
        this.atributos = atributos;
        this.metodos = metodos;

    }

}
