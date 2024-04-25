package org.com.etapa3.ClasesSemantico;

public class EntradaAtributo {
    private String name;
    private String type;
    private boolean isPublic = true;
    private int pos = 0;

    // Constructor
    public EntradaAtributo(String name, String type, boolean isPublic, int pos){
        this.name = name;
        this.type = type;
        this.isPublic = isPublic;
        this.pos = pos;
    }

    // Getters
    public String getType() {
        return type;
    }
    public boolean getPublic() {
        return isPublic;
    }
    public int getPos() {
        return pos;
    }
    public String getName() {
        return name;
    }

    // Setters


    public String imprimeAtributo(){
        return "\t\t\"tipo\": \""+this.type+"\"," +
                "\n\t\t\"public\": "+ this.isPublic +","+
                "\n\t\t\"posicion\": "+ this.pos ;
    }


}
