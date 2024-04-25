package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.Token;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class EntradaMetodo {
    private String nombre;
    private boolean isStatic = false;
    private String ret;
    private int pos;
    private Hashtable<String, EntradaParametro> parametros;
    private Hashtable<String, EntradaVariable> variables;


    // Constructor
    public EntradaMetodo(String nombre, boolean isStatic, int pos){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = null;
        this.pos = pos;
        this.parametros = new Hashtable<>();
        this.variables = new Hashtable<>();
    }

    public EntradaMetodo(String nombre, boolean isStatic, String ret, int pos){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = ret;
        this.pos = pos;
        this.parametros = new Hashtable<>();
        this.variables = new Hashtable<>();
    }

    public EntradaMetodo(){
        this.nombre = "contructor";
        this.parametros = new Hashtable<>();
        this.variables = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return nombre;
    }
    public int getPos() {
        return pos;
    }
    public Hashtable<String, EntradaParametro> getParametros() {
        return parametros;
    }
    public Hashtable<String, EntradaVariable> getVariables() {
        return variables;
    }

    // Setters
    public void setRet(String ret) {
        this.ret = ret;
    }
    // Functions

    public void insertParametro(String name, EntradaParametro parametro, Token token) {
        if(this.parametros.containsKey(name)){
            throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un parametro con el nombre \"" + name + "\" en el metodo \"" + this.nombre + "\"","insertParametro");
        }
        this.parametros.put(name, parametro);
    }

    public void insertVariable(String name, EntradaVariable variable) {
        if(this.variables.containsKey(name)){
            throw new SemantErrorException(variable.getLine(), variable.getCol(),
                    "Ya existe una variable con el nombre \"" + name + "\" en el metodo \"" + this.nombre + "\"","insertVariable");
        }
        this.variables.put(name, variable);
    }

    public void insertParametroPred(String name, EntradaParametro parametro) {
        this.parametros.put(name, parametro);
    }

    public String printJSON_Parm(){
        String json = "";
        json += "\t\t\"static\": \""+this.isStatic+"\"," +
                "\n\t\t\"retorno\": \""+ this.ret +"\","+
                "\n\t\t\"posicion\": "+ this.pos + ","+
                "\n\t\t\"paramF\": " ;
        if(!parametros.isEmpty()){
            // Obtener una lista de parametros ordenados por su posición
            List<EntradaParametro> parametrosOrdenados = new ArrayList<>(parametros.values());
            parametrosOrdenados.sort(Comparator.comparingInt(EntradaParametro::getPos));
            json +="[";
            List<String> jsonParametro = new ArrayList<>(); // Lista para almacenar JSONs de atributos
            for (EntradaParametro parametro : parametrosOrdenados) {
                jsonParametro.add("\n\t\t{\n\t\t\t\"nombre\": \"" + parametro.getName() + "\",\n" + parametro.imprimeParametro() + "\n\t\t}");
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonParametro);
            json += "\n\t\t],";
        } else {
            json +="[ ],";
        }
        json += "\n\t\t\"variables\": " ;
        if(!variables.isEmpty()){
            // Obtener una lista de variables ordenados por su posición
            List<EntradaVariable> variablesOrdenados = new ArrayList<>(variables.values());
            variablesOrdenados.sort(Comparator.comparingInt(EntradaVariable::getPos));
            json +="[";
            List<String> jsonVariable = new ArrayList<>(); // Lista para almacenar JSONs de variables
            for (EntradaVariable variable : variablesOrdenados) {
                jsonVariable.add("\n\t\t{\n\t\t\t\"nombre\": \"" + variable.getName() + "\",\n" + variable.imprimeVarMet() + "\n\t\t}");
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonVariable);
            json += "\n\t\t]";
        } else {
            json +="[ ]";
        }
        return json;
    }

    public String printJSON_Const(){
        String json = "";
        json += "\n\t\t\"paramF\": " ;
        if(!parametros.isEmpty()){
            // Obtener una lista de parametros ordenados por su posición
            List<EntradaParametro> parametrosOrdenados = new ArrayList<>(parametros.values());
            parametrosOrdenados.sort(Comparator.comparingInt(EntradaParametro::getPos));
            json +="[";
            List<String> jsonParametro = new ArrayList<>(); // Lista para almacenar JSONs de atributos
            for (EntradaParametro parametro : parametrosOrdenados) {
                jsonParametro.add("\n\t\t{\n\t\t\t\"nombre\": \"" + parametro.getName() + "\",\n" + parametro.imprimeParametro() + "\n\t\t}");
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonParametro);
            json += "\n\t\t]";
        } else {
            json +="[ ]";
        }
        return json;
    }


}
