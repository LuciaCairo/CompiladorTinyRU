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
    private EntradaMetodo constructor;
    boolean haveImpl = false;
    boolean haveStruct = false;

    // Constructor
    public EntradaStruct(String name) {
        this.name = name;
        this.metodos = new Hashtable<>();
        this.atributos = new Hashtable<>();
    }

    public EntradaStruct() {
        this.name= "start";
        this.atributos = new Hashtable<>();
        this.metodos = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return name;
    }
    public Boolean gethaveStruct() {
        return haveStruct;
    }
    public Boolean gethaveImpl() {
        return haveImpl;
    }

    // Setters
    public void setHerencia(String herencia) {
        this.herencia = herencia;
    }
    public void sethaveStruct(Boolean haveStruct) {
        this.haveStruct = haveStruct;
    }
    public void sethaveImpl(Boolean haveImpl) {
        this.haveImpl= haveImpl;
    }

    // Functions
    public void insertAtributo(String name, EntradaAtributo atributo, Token token) {
        if(this.atributos.containsKey(name)){
            if(this.name == "start"){
                throw new SemantErrorException(token.getLine(), token.getCol(),
                        "Ya existe una variable con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
            }
            throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un atributo con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
        }
        this.atributos.put(name, atributo);
    }

    public void insertMetodo(String name, EntradaMetodo metodo, Token token) {
        if(this.metodos.containsKey(name)){
            if(name.equals("constructor")){
                throw new SemantErrorException(token.getLine(), token.getCol(),
                        "Ya existe un metodo constructor en la clase \"" + this.name + "\"","insertAtributo");
            }else{
                throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un metodo con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
            }
        }
        this.metodos.put(name, metodo);
    }

    public String printJSON_Struct(){
        String json = "";
        this.constructor = this.metodos.remove("constructor");
        if(this.constructor == null){
            throw new SemantErrorException(0, 0,
                    "No se definio un constructor para la clase \"" + this.name + "\"","printJasonTabla");
        }
        json += "\t\"heredaDe\": \""+this.herencia+"\",\n\t\"constructor\": {"+ constructor.printJSON_Const() +"\n\t},";
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
        } else {
            json +="\n\t\"atributos\": [ ],";
        }
        if(!metodos.isEmpty()){
            json +="\n\t\"metodos\": [";
            List<String> jsonMetodos = new ArrayList<>(); // Lista para almacenar JSONs
            int num = 0; // Para la posicion
            for (Map.Entry<String, EntradaMetodo> entry : metodos.entrySet()) {
                String key = entry.getKey();
                EntradaMetodo value = entry.getValue();
                jsonMetodos.add("\n\t{\n\t\t\"nombre\": \""+ key + "\",\n"+value.printJSON_Parm(num)+"\n\t}");
                num += 1;

            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonMetodos);
            json += "\n\t]";
        }else {
            json +="\n\t\"metodos\": [ ]";
        }
        return json;
    }

    public String printJSON_Start(){
        String json = "";
        if(!atributos.isEmpty()){
            json +="\n\t\"atributos\": [";
            List<String> jsonAtributos = new ArrayList<>(); // Lista para almacenar JSONs de atributos
            int num = 0; // Para la posicion
            for (Map.Entry<String, EntradaAtributo> entry : atributos.entrySet()) {
                String key = entry.getKey();
                EntradaAtributo value = entry.getValue();
                jsonAtributos.add("\n\t{\n\t\t\"nombre\": \""+ key + "\",\n"+value.imprimeVar(num)+"\n\t}");
                num += 1;
            }
            json += String.join(",", jsonAtributos);
            json += "\n\t]";
        }
        return json;
    }

}