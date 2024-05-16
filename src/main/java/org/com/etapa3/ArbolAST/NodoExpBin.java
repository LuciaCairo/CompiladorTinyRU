package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

public class NodoExpBin extends NodoLiteral {
    private NodoLiteral nodoI;
    private NodoLiteral nodoD;
    private String op;

    // Constructor
    public NodoExpBin(int line, int col, NodoLiteral nodoI, String op, NodoLiteral nodoD, String type){
        super(line,col,type);
        this.nodoD = nodoD;
        this.nodoI = nodoI;
        this.op = op;
    }

    // Functions
    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Expresion Binaria\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"operador\":\""+ this.op +"\",\n"
                + space + "\"nodoIzq\": {\n"+ this.nodoI.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"nodoDer\": {\n"+ this.nodoD.printSentencia(space+"\t") +"\n" + space +"}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpBin: nodoI op nodoD
        this.nodoI.checkTypes(ts);
        this.nodoD.checkTypes(ts);
        String typeNI = this.nodoI.getNodeType();
        String typeND = this.nodoD.getNodeType();

        // Caso de expresion binaria con operadores logicos
        if(this.op.equals("||") || this.op.equals("&&") || this.op.equals("==") || this.op.equals("!=")) {
            if(!typeNI.equals(typeND)){
                throw new SemantErrorException(this.getLine(), this.getLine(),
                            "Incompatibilidad de tipos. No se puede realizar una operacion \"" + this.op +"\" " +
                                    "entre un " + typeNI + " y un " + typeND,
                            "NodoExpBin");
            }
            // Setear el tipo correspondiente
            this.setNodeType("Bool");

        } else{ // Caso de expresion binaria con operadores aritmeticos
            if(typeNI.equals("Int") && typeND.equals("Int")){
                if(this.op.equals("<") || this.op.equals(">") ||
                        this.op.equals("<=") || this.op.equals(">=")){
                    this.setNodeType("Bool");
                } else {
                    // Setear el tipo correspondiente
                    this.setNodeType("Int");
                }
            } else{
                throw new SemantErrorException(this.getLine(), this.getLine(),
                        "Incompatibilidad de tipos. No se puede realizar una operacion \"" + this.op +"\" " +
                                "entre un " + typeNI + " y un " + typeND+ ". Ambos deben ser enteros",
                        "NodoExpBin");
            }
        }
        return true;
    }
}
