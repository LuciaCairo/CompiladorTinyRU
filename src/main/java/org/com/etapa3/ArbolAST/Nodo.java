package org.com.etapa3.ArbolAST;

// Este es el nodo con la estructura general del que heredan todos los nodos
public class Nodo {
    private int line;
    private int col;
    private String value;
    private String nodeType; // Tipo de nodo
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
    public Nodo(int line,int col,String nodeType, String value){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
        this.value = value;
    }

    public Nodo(int line,int col,String name,String nodeType, String value){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
        this.value = value;
        this.name = name;
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

    // Functions
    /*

public String imprimeSentencia(){
        return "\"nodo\":\"NodoAST\"";
    }

    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        return true;
    }*/
}
