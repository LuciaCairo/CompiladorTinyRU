package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.Token;

import java.util.Hashtable;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class EntradaStruct {
    private String name;
    private String herencia = "Object";
    private Hashtable<String, EntradaAtributo> atributos;
    private Hashtable<String, EntradaMetodo> metodos;
    private EntradaMetodo constructor = null;

    // Constructor
    public EntradaStruct(String name) {
        this.name = name;
        this.metodos = new Hashtable<>();
        this.atributos = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    // Setters
    public void setHerencia(String herencia) {
        this.herencia = herencia;
    }

    // Functions
    public void insertAtributo(String name, EntradaAtributo atributo, Token token) {
        if(this.atributos.containsKey(name)){
            throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un atributo con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
        }
        this.atributos.put(name, atributo);
    }

    public String printJSON_Struct(){
        String json = "";
        json += "\t\"heredaDe\": \""+this.herencia+"\",\n\t\"constructor\": " +",";
        if(!atributos.isEmpty()){
            json +="\n\t\"atributos\": [";
            List<String> jsonAtributos = new ArrayList<>(); // Lista para almacenar JSONs de atributos
            int num = 0; // Para la posicion
            for (Map.Entry<String, EntradaAtributo> entry : atributos.entrySet()) {
                String key = entry.getKey();
                EntradaAtributo value = entry.getValue();
                jsonAtributos.add("\n\t{\n\t\t\"nombre\": \""+ key + "\",\n"+value.imprimeAtributo(num)+"\n\t}");
                num += 1;
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonAtributos);
            json += "\n\t],";
        }
        return json;
    }

}