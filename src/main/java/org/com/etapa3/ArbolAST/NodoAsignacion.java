package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

// Nodo para las asignaciones (expresion = expresion)
public class NodoAsignacion extends NodoLiteral {

    private NodoLiteral izq;
    private NodoLiteral der;


    // Constructores
    public NodoAsignacion(int line, int col, NodoLiteral izq, NodoLiteral der, String type){
        super(line, col, type);
        this.der = der;
        this.izq = izq;
    }

    @Override
    public String printSentencia(String space) {
        return "\"nodo\": \"Asignacion\",\n"
                + space + "\t\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\t\"NodoIzq\":{\n"+ this.izq.printSentencia(space+"\t\t")+"\n\t"+ space +"},\n"
                + space + "\t\"NodoDer\":{\n"+ this.der.printSentencia(space+"\t\t")+ space +"\n\t" + space + "}\n";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoAcceso: nodoI = nodoD
        // Verificar que el nodoI y el nodoD sean del mismo tipo
        // Setear el tipo correspondiente una vez que se chequeo todo, si no tirar error
        return true;
    }

}

