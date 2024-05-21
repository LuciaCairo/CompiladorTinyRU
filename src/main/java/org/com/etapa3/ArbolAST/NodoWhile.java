package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
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

    // Getters
    public LinkedList<NodoLiteral> getSentencias() {
        return sentencias;
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
            json +=  space + "\"sentencias\":[\n";
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t" + space + this.sentencias.get(i).printSentencia(space + "\t") + space + "}],\n";
            }
            json = json.substring(0, json.length() - 2) + "\n";
        } else {
            json +=  space + "\"sentencias\":[]\n";
        }
        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoWhile: while(exp){sentencias}
        this.exp.checkTypes(ts); // Chequeo la expresion
        // Verificar que el exp sea de tipo bool
        if(!(this.exp.getNodeType().equals("Bool"))){
            throw new SemantErrorException(this.exp.getLine(), this.exp.getCol(),
                    "La condicion del while debe ser de tipo Bool",
                    "sentencia");
        }
        // Hacer checkTypes de sus sentencias
        if(!this.getSentencias().isEmpty()) {
            for (NodoLiteral s : this.getSentencias()) { // Recorro las sentencias del bloque
                s.checkTypes(ts);
            }
        }
        // Setear el tipo correspondiente una vez que se chequeo
        this.setNodeType(null);
        return true;
    }

}
