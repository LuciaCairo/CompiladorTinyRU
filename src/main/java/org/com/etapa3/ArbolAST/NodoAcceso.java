package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

// Nodo para los Accesos (noodI.nodoD o nodoI[nodoD])
public class NodoAcceso extends NodoLiteral {

    private NodoLiteral nodoI;
    private NodoLiteral nodoD;


    // Constructores
    public NodoAcceso(int line, int col, NodoLiteral nodoI, NodoLiteral nodoD, String type){
        super(line, col, type);
        this.nodoD = nodoD;
        this.nodoI = nodoI;
    }

    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Acceso\",\n"
                + space + "\"nodoIzq\": {\n"+ this.nodoI.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"nodoDer\": {\n"+ this.nodoD.printSentencia(space+"\t") +"\n" + space +"}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoAcceso: nodoI.nodoD o nodoI[nodoD]
        // Chequeo del nodoI
        this.nodoI.checkTypes(ts);
        String[] palabras = nodoI.getNodeType().split(" ");
        String isArray = palabras[0];
        if(isArray.equals("Array")){ // Caso de Acceso a un array nodoI[nodoD]
            // Chequeo del nodoD
            this.nodoD.checkTypes(ts);

            // Verificar que el nodoD sea de tipo entero
            if(!this.nodoD.getNodeType().equals("Int")){
                // Si no es de tipo entero ERROR
                    throw new SemantErrorException(this.nodoD.getLine(), this.nodoD.getCol(),
                            "La posicion para acceder debe ser de tipo Int",
                            "NodoAcceso");

            }

            // Setear al NodoAcceso el tipo correspondiente
            // En este caso se quiere acceder a un elemento del array
            String type = palabras[1];
            this.setNodeType(type);

        } else { // Caso de Acceso nodoI.nodoD

            // Verificar que el nodoI sea de tipo struct y que ese struct exista en la ts
            if(!(ts.getTableStructs().containsKey(this.nodoI.getNodeType())
                    || ts.getStructsPred().containsKey(this.nodoI.getNodeType()))){
                // Si el tipo del nodoI no existe en la ts ERROR
                throw new SemantErrorException(this.nodoI.getLine(), this.nodoI.getCol(),
                        "\"" + this.nodoI.getName() +"\" no es de tipo Struct ni Array por lo que no usar para un acceso",
                        "NodoAcceso");

            }

            // Chequeo del nodoD
            this.nodoD.checkTypes(ts);
            // Verificar que el nodoD sea un metodo existente del struct (en el caso de que nodoD = llamada metodo)
            if(this.nodoD.getClass().getSimpleName().equals("NodoLlamadaMetodo")){
                NodoLlamadaMetodo nodo = (NodoLlamadaMetodo) this.nodoD;
                String nameMetodo = nodo.getMetodo();
                if(!(ts.getTableStructs().get(this.nodoI.getNodeType()).getMetodos().containsKey(nameMetodo))){
                    // Si el metodo no existe en el struct ERROR
                    throw new SemantErrorException(this.nodoD.getLine(), this.nodoD.getCol(),
                            "\"" + nameMetodo +"\" no es un metodo del struct \""+ this.nodoI.getNodeType() +"\"",
                            "NodoAcceso");

                }
            } else { // Verificar que el nodoD sea un atributo existente del struct
                if(!(ts.getTableStructs().get(this.nodoI.getNodeType()).getAtributos().containsKey(nodoD.getName()))){
                    // Si el atributo no existe en el struct ERROR
                    throw new SemantErrorException(this.nodoD.getLine(), this.nodoD.getCol(),
                            "Acceso incorrecto. No se puede acceder al atributo ya que no existe en el struct \""+ this.nodoI.getNodeType() +"\"",
                            "NodoAcceso");
                }
            }

            // Setear al NodoAcceso el tipo correspondiente
            // El tipo de un acceso es el tipo de su nodo derecho porque:
            // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
            // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
            this.setNodeType(nodoD.getNodeType());
        }

        return true;
    }

}