package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

import java.util.LinkedList;

public class NodoIf extends NodoLiteral {
    private NodoLiteral exp;
    private LinkedList<NodoLiteral> sentencias;
    private NodoElse nodoElse = null;

    // Constructor
    public NodoIf(int line, int col, NodoLiteral exp){
        super(line, col);
        this.exp = exp;
        this.sentencias = new LinkedList<>();
    }

    // Setters
    public void setNodoElse(NodoElse nodoElse) {
        this.nodoElse = nodoElse;
    }

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }

    @Override
    public String printSentencia(String space) {
        String json = "\"nodo\": \"If\",\n"
                + space + "\"expresion\": {\n"+ this.exp.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"sentencias\":[\n";
        if(!this.sentencias.isEmpty()){
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t"+ space+ this.sentencias.get(i).printSentencia(space+"\t")+space+ "},\n";
            }
            json = json.substring(0,json.length()-2);
        }
        json +="\n" + space + "],\n"
                + space + this.nodoElse.printSentencia(space) + space +"},\n";
        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoIf: if(exp){sentencias} else{sentencias}
        // Verificar que el exp sea de tipo bool
        // Hacer checkTypes de sus sentencias
        // Hacer checkTypes de sus sentenciasElse
        // Setear el tipo correspondiente una vez que se chequeo todo, si no tirar error
        return true;
    }

}
