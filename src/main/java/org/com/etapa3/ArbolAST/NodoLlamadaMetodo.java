package org.com.etapa3.ArbolAST;

import java.util.LinkedList;

public class NodoLlamadaMetodo extends NodoLiteral{
    private String typeStruct;
    private String nameStruct;
    private String metodo;
    private LinkedList<NodoLiteral> argumentos;


    public NodoLlamadaMetodo(int line, int col, String nameStruct,String typeStruct, String metodo){
        super(line, col);
        this.nameStruct = nameStruct;
        this.typeStruct = typeStruct;
        this.metodo = metodo;
        this.argumentos = new LinkedList<>();
    }

    // Getters

    public String getTypeStruct() {
        return typeStruct;
    }

    public String getMetodo() {
        return metodo;
    }
    // Setters

    // Functions
    public void insertArgumento(NodoLiteral argumento) {
        this.argumentos.add(argumento);
    }

    /*public String printNodoMet(){
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
    }*/

}
