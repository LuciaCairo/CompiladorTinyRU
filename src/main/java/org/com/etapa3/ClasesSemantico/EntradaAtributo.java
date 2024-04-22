package org.com.etapa3.ClasesSemantico;

public class EntradaAtributo {
    private String name;
    private String type;
    private boolean isPublic = true;
    private int pos = 0;

    // Constructor
    public EntradaAtributo(String name, String type, boolean isPublic){
        this.name = name;
        this.type = type;
        this.isPublic = isPublic;
    }

    public String imprimeAtributo(int num){
        return "\t\t\"tipo\": \""+this.type+"\"," +
                "\n\t\t\"public\": "+ this.isPublic +","+
                "\n\t\t\"posicion\": "+ num ;
    }

}
