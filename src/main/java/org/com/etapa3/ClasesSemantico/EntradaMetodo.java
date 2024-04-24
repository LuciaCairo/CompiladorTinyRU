package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.Token;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class EntradaMetodo {
    private String nombre;
    private boolean isStatic = false;
    private String ret;
    private int pos;
    private Hashtable<String, EntradaParametro> parametros;


    // Constructor
    public EntradaMetodo(String nombre, boolean isStatic, int pos){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = null;
        this.pos = pos;
        this.parametros = new Hashtable<>();
    }

    public EntradaMetodo(String nombre, boolean isStatic, String ret, int pos){
        this.nombre = nombre;
        this.isStatic = isStatic;
        this.ret = ret;
        this.pos = pos;
        this.parametros = new Hashtable<>();
    }

    public EntradaMetodo(){
        this.nombre = "contructor";
        this.parametros = new Hashtable<>();
    }

    // Getters
    public String getName() {
        return nombre;
    }

    // Setters
    public void setRet(String ret) {
        this.ret = ret;
    }
    // Functions

    public void insertParametro(String name, EntradaParametro parametro, Token token) {
        if(this.parametros.containsKey(name)){
            throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un parametro con el nombre \"" + name + "\" en el Metodo \"" + this.nombre + "\"","insertParametro");
        }
        this.parametros.put(name, parametro);
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
            json +=" [";
            List<String> jsonParametro = new ArrayList<>(); // Lista para almacenar JSONs
            for (Map.Entry<String, EntradaParametro> entry : parametros.entrySet()) {
                String key = entry.getKey();
                EntradaParametro value = entry.getValue();
                jsonParametro.add("\n\t\t\t{\n\t\t\t\"nombre\": \""+ key + "\",\n"+value.imprimeParametro()+"\n\t\t\t}");
            }
            // Unir los JSONs de atributos en una cadena
            json += String.join(",", jsonParametro);
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
            json +=" [";
            List<String> jsonParametro = new ArrayList<>(); // Lista para almacenar JSONs
            int num = 0; // Para la posicion
            for (Map.Entry<String, EntradaParametro> entry : parametros.entrySet()) {
                String key = entry.getKey();
                EntradaParametro value = entry.getValue();
                jsonParametro.add("\n\t\t\t{\n\t\t\t\"nombre\": \""+ key + "\",\n"+value.imprimeParametro()+"\n\t\t\t}");
                num += 1;
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
