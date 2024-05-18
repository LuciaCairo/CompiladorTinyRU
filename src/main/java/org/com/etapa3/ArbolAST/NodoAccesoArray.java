package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

// Nodo para los Accesos de Array (nodo[exp])
public class NodoAccesoArray extends NodoLiteral {

    private NodoLiteral nodo;
    private NodoLiteral exp;


    // Constructores
    public NodoAccesoArray(int line, int col, NodoLiteral nodo, NodoLiteral exp, String type){
        super(line, col, type);
        this.nodo = nodo;
        this.exp = exp;
    }

    // Functions
    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Acceso Array\",\n"
                + space + "\"nodo\": {\n"+ this.nodo.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"indice\": {\n"+ this.exp.printSentencia(space+"\t") +"\n" + space +"}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoAccesoArray: nodo[exp]
        // Chequeo del nodo
        this.nodo.checkTypes(ts);

        // Verifico que sea de tipo Array
        String[] palabras = nodo.getNodeType().split(" ");
        String isArray = palabras[0];
        String type = palabras[1];
        if(isArray.equals("Array")){
            // Chequeo de la expresion
            this.exp.checkTypes(ts);

            // Verificar que al exp sea de tipo entero
            if(!this.exp.getNodeType().equals("Int")){
                // Si no es de tipo entero ERROR
                    throw new SemantErrorException(this.exp.getLine(), this.exp.getCol(),
                            "La posicion para acceder debe ser de tipo Int",
                            "NodoAcceso");
            }

            // Setear al NodoAccesoArray el tipo correspondiente
            // El tipo de un acceso array es el tipo del elemento que se accede:
            // Ejemplo: Array Int a, entonces a[1] esta accediento a un Int
            this.setNodeType(type);
            this.setParent(type); // En caso de acceso


        } else {
            throw new SemantErrorException(this.nodo.getLine(), this.nodo.getCol(),
                    "Acceso incorrecto. No se puede acceder ya que \"" + this.nodo.getName() +
                            "\" No es de tipo Array.", "NodoAcceso");
        }
        return true;
    }
}