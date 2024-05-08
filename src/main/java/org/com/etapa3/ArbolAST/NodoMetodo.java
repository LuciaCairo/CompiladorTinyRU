package org.com.etapa3.ArbolAST;

import java.util.LinkedList;

public class NodoMetodo extends Nodo{
    private String name;
    private LinkedList<NodoSentencia> sentencias;


    public NodoMetodo(int line,int col,String name, Nodo parent){
        super(line, col, parent);
        this.name = name;
        this.sentencias = new LinkedList<>();
    }

    // Getters
    public String getName() {
        return name;
    }
    public LinkedList<NodoSentencia> getSentencias() {
        return sentencias;
    }

    // Setters

    // Functions
    public void insertSentencia(NodoSentencia sentencia) {
        this.sentencias.add(sentencia);
    }

    public String printNodoMet(){
        String json = "";
        json += "\t\t\t\t\"sentencias\":[\n";
        if(!this.sentencias.isEmpty()){
            for (int i = 0; i < this.sentencias.size(); i++) {
                json +="\t\t\t\t{\n\t\t\t\t\t"+ this.sentencias.get(i).printSentencia("\t\t\t\t\t")+"\t\t\t\t},";
            }
            json = json.substring(0,json.length()-1);
        }
        json +="\n\t\t\t\t]\n";
        return json;
    }

}
