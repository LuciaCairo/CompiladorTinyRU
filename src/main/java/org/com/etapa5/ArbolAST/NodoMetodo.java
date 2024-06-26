package org.com.etapa5.ArbolAST;

import java.util.LinkedList;

public class NodoMetodo extends Nodo{
    private String name;
    private LinkedList<NodoLiteral> sentencias;


    public NodoMetodo(int line,int col,String name){
        super(line, col);
        this.name = name;
        this.sentencias = new LinkedList<>();
    }

    // Getters
    public String getName() {
        return name;
    }
    public LinkedList<NodoLiteral> getSentencias() {
        return sentencias;
    }

    // Setters

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }

    public String printNodoMet(){
        String json = "";
        if(!this.sentencias.isEmpty()){
            json += "\t\t\t\t\"sentencias\":[\n";
            for (int i = 0; i < this.sentencias.size(); i++) {
                json +="\t\t\t\t{\n\t\t\t\t\t"+ this.sentencias.get(i).printSentencia("\t\t\t\t\t")+"\n\t\t\t\t},\n";
            }
            json = json.substring(0,json.length()-2);
            json +="\n\t\t\t\t]\n";
        } else {
            json += "\t\t\t\t\"sentencias\":[]\n";
        }
        return json;
    }

}
