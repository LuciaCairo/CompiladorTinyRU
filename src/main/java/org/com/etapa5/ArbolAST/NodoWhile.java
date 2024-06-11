package org.com.etapa5.ArbolAST;

import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;
import org.com.etapa5.CodeGenerator;

import java.util.LinkedList;

public class NodoWhile extends NodoLiteral {

    private NodoLiteral exp;
    private LinkedList<NodoLiteral> sentencias;
    public static int count = -1;

    // Constructor
    public NodoWhile(int line, int col, NodoLiteral exp){
        super(line, col);
        this.exp = exp;
        this.sentencias = new LinkedList<>();
    }

    // Getters
    public LinkedList<NodoLiteral> getSentencias() {
        return sentencias;
    }

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }

    @Override
    public String printSentencia(String space) {
        String json = "\"nodo\": \"While\",\n"
                + space + "\"expresion\": {\n" + this.exp.printSentencia(space + "\t") + "\n" + space + "},\n";
        if (!this.sentencias.isEmpty() && !(this.sentencias.getFirst() == null) ) {
            json +=  space + "\"sentencias\":[\n";
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t" + space + this.sentencias.get(i).printSentencia(space + "\t") + space + "}],\n";
            }
            json = json.substring(0, json.length() - 2) + "\n";
        } else {
            json +=  space + "\"sentencias\":[]\n";
        }
        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoWhile: while(exp){sentencias}
        this.exp.checkTypes(ts); // Chequeo la expresion
        // Verificar que el exp sea de tipo bool
        if(!(this.exp.getNodeType().equals("Bool"))){
            throw new SemantErrorException(this.exp.getLine(), this.exp.getCol(),
                    "La condicion del while debe ser de tipo Bool",
                    "sentencia");
        }
        // Hacer checkTypes de sus sentencias
        if(!this.getSentencias().isEmpty()) {
            for (NodoLiteral s : this.getSentencias()) { // Recorro las sentencias del bloque
                s.checkTypes(ts);
            }
        }
        // Setear el tipo correspondiente una vez que se chequeo
        this.setNodeType(null);
        return true;
    }

    @Override
    public String generateNodeCode(TablaSimbolos ts) {
        count = ++count;
        StringBuilder code = new StringBuilder();
        code.append("\n\t# NODO WHILE \n");

        // Generar la etiqueta para el comienzo del bucle
        code.append("\twhile_start_"+ count +":\n");
        // Generar el código para la evaluación de la expresión de la condición
        code.append(this.exp.generateNodeCode(ts));
        int condReg = CodeGenerator.registerCounter;

        // Generar el código para el salto a la salida del bucle si la condición es falsa
        code.append("\tbeq $t").append(condReg).append(", $zero, ").append("while_end_"+ count).append("\n");

        // Generar el código para las sentencias dentro del bucle
        for (NodoLiteral sentencia : this.sentencias) {
            code.append(sentencia.generateNodeCode(ts));
        }

        // Generar el salto al comienzo del bucle para la próxima iteración
        code.append("\tj ").append("while_start_"+ count).append("\n");

        // Generar la etiqueta para la salida del bucle
        code.append("\twhile_end_"+ count).append(":\n");

        return code.toString();
    }


}
