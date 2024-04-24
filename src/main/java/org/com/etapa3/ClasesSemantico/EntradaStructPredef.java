package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.Token;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
            json +="\t\"metodos\": [";
            List<String> jsonMetodos = new ArrayList<>(); // Lista para almacenar JSONs
            int num = 0; // Para la posicion
            for (Map.Entry<String, EntradaMetodo> entry : metodos.entrySet()) {
                String key = entry.getKey();
                EntradaMetodo value = entry.getValue();
                jsonMetodos.add("\n\t{\n\t\t\"nombre\": \""+ key + "\",\n"+value.printJSON_Parm()+"\n\t}");
                num += 1;
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
