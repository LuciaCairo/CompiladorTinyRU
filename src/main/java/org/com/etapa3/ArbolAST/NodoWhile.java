package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

import java.util.LinkedList;

public class NodoWhile extends NodoLiteral {

    private NodoLiteral exp;
    private LinkedList<NodoLiteral> sentencias;

    // Constructor
    public NodoWhile(int line, int col, NodoLiteral exp){
        super(line, col);
        this.exp = exp;
        this.sentencias = new LinkedList<>();
    }

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }


    @Override
    public String printSentencia(String space) {
        String json = "\"nodo\": \"While\",\n"
                + space + "\"expresion\": {\n" + this.exp.printSentencia(space + "\t") + "\n" + space + "},\n";
        if (!this.sentencias.isEmpty() && !(this.sentencias.getFirst() == null) ) {
            System.out.println(sentencias);
            json +=  space + "\"sentencias\":[\n";
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t" + space + this.sentencias.get(i).printSentencia(space + "\t") + space + "},\n";
            }
            json = json.substring(0, json.length() - 2);
        } else {
            json +=  space + "\"sentencias\":[]\n";
        }
        return json;
    }


    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoWhile: while(exp){sentencias}
        // Verificar que el exp sea de tipo bool
        // Hacer checkTypes de sus sentencias
        return true;
    }
}
