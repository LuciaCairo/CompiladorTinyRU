package org.com.etapa5.TablaDeSimbolos;

import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.Token;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Comparator;

public class EntradaStructPredef {
    private String name;
    private Hashtable<String, EntradaMetodo> metodos;

    // Constructor
    public EntradaStructPredef(String name) {
        this.name = name;
        this.metodos = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return name;
    }
    public Hashtable<String, EntradaMetodo> getMetodos() {
        return this.metodos;
    }

    // Setters

    // Functions
    public void insertMetodo(String name, EntradaMetodo metodo, Token token) {
        if(this.metodos.containsKey(name)){
            throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un metodo con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
        }
        this.metodos.put(name, metodo);
    }

    public void insertMetodoPred(String name, EntradaMetodo metodo) {
        this.metodos.put(name, metodo);
    }

    public String printJSON_StructPredef(){
        String json = "";
        if(!metodos.isEmpty()){
            // Obtener una lista de métodos ordenados por su posición
            List<EntradaMetodo> metodosOrdenados = new ArrayList<>(metodos.values());
            metodosOrdenados.sort(Comparator.comparingInt(EntradaMetodo::getPos));
            json +="\t\"metodos\": [";
            List<String> jsonMetodos = new ArrayList<>(); // Lista para almacenar JSONs
            for (EntradaMetodo metodo : metodosOrdenados) {
                jsonMetodos.add("\n\t{\n\t\t\"nombre\": \"" + metodo.getName() + "\",\n" + metodo.printJSON_Parm() + "\n\t}");
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonMetodos);
            json += "\n\t]";
        }else {
            json +="\t\"metodos\": [ ]";
        }
        return json;
    }
}
