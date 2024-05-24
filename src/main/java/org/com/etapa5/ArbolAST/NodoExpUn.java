package org.com.etapa5.ArbolAST;

import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

// Nodo para expresiones unarias (por ejemplo: ++a, --a, !a)
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
                + space + "\"expresion\": {\n"+ this.exp.printSentencia(space+"\t") +"\n"+ space +"}";

    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpUn: opUnario exp
        this.exp.checkTypes(ts);
        // Verificar que exp sea "Bool" cuando el operador es "!"
        if(op.equals("!")){
            if(!this.exp.getNodeType().equals("Bool")){
                throw new SemantErrorException(this.getLine(),this.getCol(),
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" con un " + exp.getNodeType(),
                        "expCompuesta1");
            }
            // Setear el tipo correspondiente
            this.setNodeType("Bool");

        } else { // Verificar que exp sea "Int" en todos los demas casos ++ --
            if(!this.exp.getNodeType().equals("Int")){
                throw new SemantErrorException(this.getLine(),this.getCol(),
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" con un " + exp.getNodeType(),
                        "expCompuesta1");
            }
            // Setear el tipo correspondiente
            this.setNodeType("Int");
        }

        return true;
    }
}
