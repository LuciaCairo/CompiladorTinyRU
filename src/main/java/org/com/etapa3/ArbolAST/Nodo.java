package org.com.etapa3.ArbolAST;

// Este es el nodo con la estructura general del que heredan todos los nodos
public class Nodo {
    private int line;
    private int col;
    private String value = null;
    private String nodeType; // Tipo de nodo
    private Nodo parent; // Nodo padre
    private String name;


    // Constructores
    public Nodo(int line,int col){
        this.line = line;
        this.col = col;
    }
    public Nodo(int line,int col,String nodeType){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
    }
    public Nodo(int line,int col, Nodo parent){
        this.line = line;
        this.col = col;
        this.parent = parent;
    }
    public Nodo(int line,int col, String nodeType, Nodo parent, String value){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
        this.parent = parent;
    }

    // Getters
    public int getCol() {
        return col;
    }
    public int getLine() {
        return line;
    }
    public String getNodeType() {
        return nodeType;
    }
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }

    // Setters
    public void setParent(Nodo parent) {
        this.parent = parent;
    }

    // Functions
    /*

public String imprimeSentencia(){
        return "\"nodo\":\"NodoAST\"";
    }

    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        return true;
    }*/
}
