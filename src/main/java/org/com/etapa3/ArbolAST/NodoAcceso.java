package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

// Nodo para los Accesos (noodI.nodoD)
public class NodoAcceso extends NodoLiteral {

    private NodoLiteral nodoI;
    private NodoLiteral nodoD;


    // Constructores
    public NodoAcceso(int line, int col, NodoLiteral nodoI, NodoLiteral nodoD, String type){
        super(line, col, type);
        this.nodoD = nodoD;
        this.nodoI = nodoI;
    }

    // Functions
    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Acceso\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"nodoIzq\": {\n"+ this.nodoI.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"nodoDer\": {\n"+ this.nodoD.printSentencia(space+"\t") +"\n" + space +"}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts) {
        // NodoAcceso: nodoI.nodoD
        // Chequeo del nodoI
        if (!this.getParent().isEmpty()) {
            this.nodoI.setParent(this.getParent());
        }
        this.nodoI.checkTypes(ts);
        String[] palabras = this.nodoI.getNodeType().split(" ");
        String isArray = palabras[0];
        if(isArray.equals("Array")){
            this.nodoD.setParent("Array");
        } else {this.nodoD.setParent(nodoI.getNodeType());}


        // Verificar que el nodoI sea de tipo struct y que ese struct exista en la ts
        if (ts.getTableStructs().containsKey(this.nodoI.getNodeType())) {
            if (this.nodoD.getClass().getSimpleName().equals("NodoLiteral")) {
                // Verificar que el nodoD sea un atributo existente del struct
                if (!(ts.getTableStructs().get(this.nodoI.getNodeType()).getAtributos().containsKey(nodoD.getName()))) {
                    // Si el atributo no existe en el struct ERROR
                    throw new SemantErrorException(this.nodoD.getLine(), this.nodoD.getCol(),
                            "Acceso incorrecto. No se puede acceder al atributo \"" + this.nodoD.getName() +
                                    "\" ya que no existe en el struct \"" + this.nodoI.getNodeType() + "\"",
                            "NodoAcceso");
                }
                String type = ts.getTableStructs().get(this.nodoI.getNodeType()).getAtributos().get(nodoD.getName()).getType();
                this.nodoD.setNodeType(type);
            } else {
                // Chequeo del nodoD
                this.nodoD.checkTypes(ts);
            }
        } else if (this.nodoI.getNodeType().equals("IO") || this.nodoI.getNodeType().equals("Str") ||
                isArray.equals("Array")) {
            if (this.nodoD.getClass().getSimpleName().equals("NodoLiteral")) {
                throw new SemantErrorException(this.nodoD.getLine(), this.nodoD.getCol(),
                        "Acceso incorrecto. No se puede acceder al atributo ya que no existe en el struct \"" + this.nodoI.getNodeType() + "\"",
                        "NodoAcceso");
            } else {
                // Chequeo del nodoD
                this.nodoD.checkTypes(ts);
            }
        } else {
            throw new SemantErrorException(this.nodoI.getLine(), this.nodoI.getCol(),
                    "\"" + this.nodoI.getName() + "\" no es de tipo Struct por lo que no se puede usar usar para un acceso",
                    "NodoAcceso");
        }


        // Setear al NodoAcceso el tipo correspondiente
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
        this.setNodeType(nodoD.getNodeType());

        return true;
    }
}