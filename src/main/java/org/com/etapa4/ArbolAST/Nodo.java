package org.com.etapa4.ArbolAST;

import java.util.LinkedList;
import org.com.etapa4.*;

// Este es el nodo con la estructura general del que heredan todos los nodos
public class Nodo {
    private int line;
    private int col;
    private String value;
    private String nodeType;
    private String name;
    private String parent = "";
    private LinkedList<NodoLiteral> sentencias;


    // Constructores
    public Nodo(int line,int col){
        this.line = line;
        this.col = col;
        this.sentencias = new LinkedList<>();
    }
    public Nodo(int line,int col,String nodeType){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
        this.sentencias = new LinkedList<>();
    }
    public Nodo(int line,int col,String nodeType, String value){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
        this.value = value;
        this.sentencias = new LinkedList<>();
    }

    public Nodo(int line,int col,String name,String nodeType, String value){
        this.line = line;
        this.col = col;
        this.nodeType = nodeType;
        this.value = value;
        this.name = name;
        this.sentencias = new LinkedList<>();
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
    public void setParent(String parent) {
        this.parent = parent;
    }

    // Setters
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
    public String getParent() {
        return parent;
    }

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }

    public boolean checkTypes(TablaSimbolos ts){
        // Esta funcion es diferente para cada nodo
        // En todos los casos setear los tipos correspondientes una vez que se chequearon
        return true;
    }
}
