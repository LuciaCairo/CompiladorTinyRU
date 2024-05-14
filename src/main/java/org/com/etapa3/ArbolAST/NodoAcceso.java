package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

// Nodo para las asignaciones (expresion = expresion)
public class NodoAcceso extends NodoLiteral {

    private NodoLiteral izq;
    private NodoLiteral der;


    // Constructores
    public NodoAcceso(int line, int col, NodoLiteral izq, NodoLiteral der, String type){
        super(line, col, type);
        this.der = der;
        this.izq = izq;
    }

    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Acceso\",\n"
                + space + "\"nodoIzq\": {\n"+ this.izq.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"nodoDer\": {\n"+ this.der.printSentencia(space+"\t") +"\n" + space +"}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoAcceso: nodoI.nodoD
        // Verificar que el nodoI sea de tipo struct y que ese struct exista en la ts
        // Verificar que el nodoD sea un atributo del struct que exista en la ts
        return true;
    }

}