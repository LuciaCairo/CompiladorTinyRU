package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.Token;

import java.util.Hashtable;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class EntradaStruct {
    private String name;
    private String herencia = "Object";
    private Hashtable<String, EntradaAtributo> atributos;
    private Hashtable<String, EntradaMetodo> metodos;
    private Hashtable<String, EntradaVariable> variables;
    private EntradaMetodo constructor;
    private int col, line;
    boolean haveImpl = false;
    boolean haveStruct = false;
    boolean haveConst = false;
    boolean check = false;

    // Constructor
    public EntradaStruct(String name, int line, int col) {
        this.name = name;
        this.col = col;
        this.line = line;
        this.metodos = new Hashtable<>();
        this.atributos = new Hashtable<>();
        this.variables = new Hashtable<>();
    }

    public EntradaStruct(int line, int col) {
        this.name= "start";
        this.col = col;
        this.line = line;
        this.haveImpl = true;
        this.haveStruct = true;
        this.haveConst = true;
        this.atributos = new Hashtable<>();
        this.metodos = new Hashtable<>();
        this.variables = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getHerencia() {
        return herencia;
    }
    public int getLine() {
        return line;
    }
    public int getCol() {
        return col;
    }
    public Boolean gethaveStruct() {
        return haveStruct;
    }
    public Boolean gethaveImpl() {
        return haveImpl;
    }
    public Boolean gethaveConst() {
        return haveConst;
    }
    public Hashtable<String, EntradaMetodo> getMetodos() {
        return this.metodos;
    }
    public Hashtable<String, EntradaAtributo> getAtributos() {
        return this.atributos;
    }
    public Hashtable<String, EntradaVariable> getVariables() {
        return this.variables;
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
    public void setHaveConst(Boolean haveConst) {
        this.haveConst= haveConst;
    }
    public void setCheck(Boolean check) {
        this.check= check;
    }

    // Functions
    public void insertAtributo(String name, EntradaAtributo atributo) {
        if(this.atributos.containsKey(name)){
            throw new SemantErrorException(atributo.getLine(), atributo.getCol(),
                    "Ya existe un atributo con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
        }
        this.atributos.put(name, atributo);
    }

    public void insertAtributoHeredado(String name, EntradaAtributo atributo) {
        this.atributos.put(name, atributo);
    }

    public void insertVariable(String name, EntradaVariable variable) {
        if(this.variables.containsKey(name)){
            throw new SemantErrorException(variable.getLine(), variable.getCol(),
                        "Ya existe una variable con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
        }
        this.variables.put(name, variable);
    }

    public void insertMetodo(String name, EntradaMetodo metodo) {
        if(this.metodos.containsKey(name)){
            if(name.equals("constructor")){
                throw new SemantErrorException(metodo.getLine(), metodo.getCol(),
                        "Ya existe un metodo constructor en la clase \"" + this.name + "\"","insertAtributo");
            }else{
                throw new SemantErrorException(metodo.getLine(), metodo.getCol(),
                    "Ya existe un metodo con el nombre \"" + name + "\" en la clase \"" + this.name + "\"","insertAtributo");
            }
        }
        this.metodos.put(name, metodo);
    }

    public String printJSON_Struct(){
        String json = "";
        this.constructor = this.metodos.remove("constructor");
        json += "\t\"heredaDe\": \""+this.herencia+"\",\n\t\"constructor\": {"+ constructor.printJSON_Const() +"\n\t},";
        if(!atributos.isEmpty()){
            // Obtener una lista de atributos ordenados por su posición
            List<EntradaAtributo> atributosOrdenados = new ArrayList<>(atributos.values());
            atributosOrdenados.sort(Comparator.comparingInt(EntradaAtributo::getPos));
            json +="\n\t\"atributos\": [";
            List<String> jsonAtributos = new ArrayList<>(); // Lista para almacenar JSONs de atributos
            for (EntradaAtributo atributo : atributosOrdenados) {
                jsonAtributos.add("\n\t{\n\t\t\"nombre\": \"" + atributo.getName() + "\",\n" + atributo.imprimeAtributo() + "\n\t}");
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonAtributos);
            json += "\n\t],";
        } else {
            json +="\n\t\"atributos\": [ ],";
        }
        if(!metodos.isEmpty()){
            // Obtener una lista de métodos ordenados por su posición
            List<EntradaMetodo> metodosOrdenados = new ArrayList<>(metodos.values());
            metodosOrdenados.sort(Comparator.comparingInt(EntradaMetodo::getPos));
            json +="\n\t\"metodos\": [";
            List<String> jsonMetodos = new ArrayList<>(); // Lista para almacenar JSONs
            for (EntradaMetodo metodo : metodosOrdenados) {
                jsonMetodos.add("\n\t{\n\t\t\"nombre\": \"" + metodo.getName() + "\",\n" + metodo.printJSON_Parm() + "\n\t}");
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
        if(!variables.isEmpty()){
            List<EntradaVariable> variablesOrdenados = new ArrayList<>(variables.values());
            variablesOrdenados.sort(Comparator.comparingInt(EntradaVariable::getPos));
            json +="\n\t\"variables\": [";
            List<String> jsonVariables = new ArrayList<>();
            for (EntradaVariable variable : variablesOrdenados) {
                jsonVariables.add("\n\t{\n\t\t\"nombre\": \"" + variable.getName() + "\",\n" + variable.imprimeVar() + "\n\t}");
            }
            json += String.join(",", jsonVariables);
            json += "\n\t]";
        }
        return json;
    }

}