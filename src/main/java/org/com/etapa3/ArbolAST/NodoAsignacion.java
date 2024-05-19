package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

// Nodo para las asignaciones (expresion = expresion)
public class NodoAsignacion extends NodoLiteral {

    private NodoLiteral nodoI;
    private NodoLiteral nodoD;


    // Constructores
    public NodoAsignacion(int line, int col, NodoLiteral nodoI, NodoLiteral nodoD, String type){
        super(line, col, type);
        this.nodoD = nodoD;
        this.nodoI = nodoI;
    }

    @Override
    public String printSentencia(String space) {
        return "\"nodo\": \"Asignacion\",\n"
                + space + "\t\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\t\"NodoIzq\":{\n"+ this.nodoI.printSentencia(space+"\t\t")+"\n\t"+ space +"},\n"
                + space + "\t\"NodoDer\":{\n"+ this.nodoD.printSentencia(space+"\t\t")+ space +"\n\t" + space + "}\n";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoAsignacion: nodoI = nodoD
        // Chequeo del nodoI
        this.nodoI.checkTypes(ts);
        // Chequeo del nodoD

        this.nodoD.checkTypes(ts);
        // Verificar que el nodoI y el nodoD sean del mismo tipo
        String typeNI = nodoI.getNodeType();
        String typeND = nodoD.getNodeType();

        if(!typeNI.equals(typeND) ){
            System.out.println("NodoDer"+typeND);
            System.out.println(typeNI);
            if(!(typeNI == "Int"||
                    typeNI=="Str"||
                    typeNI=="Char"||
                    typeNI=="Bool")){
                System.out.println("YES");
                if((typeND.equals("nil")) || (!(typeND=="Int" || typeND =="Str"|| typeND=="Char"||typeND=="Bool"))) {
                    return true;
                } else{
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "Incompatibiliddddad de tipos. No se puede asignar un objeto de tipo " + typeND + " a la variable '"+nodoI.getName()+"' definida de tipo " + typeNI,
                            "nodoAsignacion");
                }

            }
            throw new SemantErrorException(this.getLine(), this.getCol(),
                    "Incompatibilidad de tipos. No se puede asignar un objeto de tipo " + typeND + " a la variable '"+nodoI.getName()+"' definida de tipo " + typeNI,
                    "nodoAsignacion");
        }
        // Setear el tipo
        this.setNodeType(typeNI);
        return true;
    }

}

