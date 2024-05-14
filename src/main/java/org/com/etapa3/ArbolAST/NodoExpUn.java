package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

// Nodo para expresiones unarias (por ejemplo: ++a, --a, !a, etc)
public class NodoExpUn extends NodoLiteral {

    private String op; // Operador unario y
    private NodoLiteral exp; // Expresión a la que se aplica ese operador

    // Constructores
    public NodoExpUn(int line, int col, String type, NodoLiteral exp, String op){
        super(line,col,type);
        this.exp = exp;
        this.op = op;
    }


    // Functions
    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Expresion Unaria\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"valor\":\""+ this.getNodeType() +"\",\n"
                + space + "\"operador\":\""+ this.op +"\",\n"
                + space + "\"expresion\": {\n"+ this.exp.printSentencia(space+"\t") +"\"";

    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpUn: opUnario exp
        // Verificar que exp sea "Bool" cuando el operador es "!"
        // Verificar que exp sea "Int" en todos los demas casos
        return true;
    }
}
