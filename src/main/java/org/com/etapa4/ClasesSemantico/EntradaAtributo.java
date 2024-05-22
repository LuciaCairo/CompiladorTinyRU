package org.com.etapa4.ClasesSemantico;

public class EntradaAtributo {
    private String name;
    private String type;
    private boolean isPublic = true;
    private int pos = 0;
    private int line, col;

    // Constructor
    public EntradaAtributo(String name, String type, boolean isPublic, int pos, int line, int col){
        this.name = name;
        this.type = type;
        this.isPublic = isPublic;
        this.pos = pos;
        this.line = line;
        this.col = col;
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
    public int getLine() {
        return line;
    }
    public int getCol() {
        return col;
    }

    // Setters
    public void setPos(int pos) {
        this.pos = pos;
    }

    public String imprimeAtributo(){
        return "\t\t\"tipo\": \""+this.type+"\"," +
                "\n\t\t\"public\": "+ this.isPublic +","+
                "\n\t\t\"posicion\": "+ this.pos ;
    }


}
