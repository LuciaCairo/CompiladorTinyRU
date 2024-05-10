package org.com.etapa3.ArbolAST;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class NodoStruct extends Nodo{
    private String name;
    private HashMap<String, NodoMetodo> metodos;
    //private NodoMetodo constructor;

    // Constructor
    public NodoStruct(int line, int col, String name){
        super(line, col);
        this.name = name;
        this.metodos = new HashMap<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    // Setters

    // Functions

    public void insertMetodo(String name, NodoMetodo nodo) {
        this.metodos.put(name, nodo);
    }

    public String printNodoStruct(){
        String json = "";
        if(!metodos.isEmpty()){
            json += "\t\"metodos\": [";
            for(Map.Entry<String, NodoMetodo> entry : metodos.entrySet()) {
                String key = entry.getKey();
                NodoMetodo value = entry.getValue();
                json += "\n\t{\n\t\t\"nombre\":\""+ value.getName() + "\",\n\t\t\"Bloque\": {\n"+ value.printNodoMet() + "\t\t}\n\t},";
            }
            json = json.substring(0,json.length()-1);
            json += "\n\t]";
        } else {
            json += "\t\"metodos\": [ ]\n";
        }
        return json;
    }

    /*
    public HashMap<String, NodoMetodo> getMetodos() {
        return metodos;
    }



    public void putConstantes(String nombre, NodoSentencia cte) {
        this.constantes.put(nombre, cte);
    }

    public void setConstructor(NodoMetodo constructor) {
        this.constructor = constructor;
    }

    public NodoMetodo getConstructor() {
        return constructor;
    }

    public HashMap<String, NodoSentencia> getConstantes() {
        return constantes;
    }




    @Override
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        if(!constantes.isEmpty()){
            for(Map.Entry<String, NodoSentencia> entry : constantes.entrySet()) {
                String key = entry.getKey();
                NodoAsignacion value = (NodoAsignacion) entry.getValue();
                value.verifica(ts);
            }

        }
        if(!metodos.isEmpty()){

            for(Map.Entry<String, NodoMetodo> entry : metodos.entrySet()) {
                String key = entry.getKey();
                NodoMetodo value = entry.getValue();
                value.verifica(ts);
            }

        }
        if(this.constructor != null){
            this.constructor.verifica(ts);
        }
        return true;
    }*/


}

