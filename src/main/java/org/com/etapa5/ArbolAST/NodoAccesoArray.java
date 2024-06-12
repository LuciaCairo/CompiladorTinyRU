package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

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
                + space + "\"array\": {\n"+ this.nodo.printSentencia(space+"\t") +"\n" + space +"},\n"
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
            String type = palabras[1];
            this.setNodeType(type);
            this.setParent(type); // En caso de acceso


        } else {
            throw new SemantErrorException(this.nodo.getLine(), this.nodo.getCol(),
                    "Acceso incorrecto. No se puede acceder ya que \"" + this.nodo.getName() +
                            "\" No es de tipo Array.", "NodoAcceso");
        }
        return true;
    }

    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        code.append("\n\t# NODO ACCESO ARRAY \n");
        // Generar código para evaluar el nodo que representa el arreglo (nodo)
        code.append(this.nodo.generateNodeCode(ts));
        int arrayReg = CodeGenerator.getBefRegister();

        // Generar código para evaluar la expresión que representa el índice (exp)
        code.append(this.exp.generateNodeCode(ts));
        int indexReg = CodeGenerator.registerCounter;

        // Calcular la dirección del elemento del array
        // Asumimos que cada elemento del array es de 4 bytes (tamaño de una palabra en MIPS)
        int elementSize = 4; // Tamaño en bytes de cada elemento del array
        code.append("mul $t").append(indexReg).append(", $t").append(indexReg).append(", ").append(elementSize).append("\n");
        code.append("add $t").append(arrayReg).append(", $t").append(arrayReg).append(", $t").append(indexReg).append("\n");

        // Cargar el valor del elemento del array en un nuevo registro
        int newReg = CodeGenerator.getNextRegister();
        code.append("lw $t").append(newReg).append(", 0($t").append(arrayReg).append(")\n");

        return code.toString();
    }
}