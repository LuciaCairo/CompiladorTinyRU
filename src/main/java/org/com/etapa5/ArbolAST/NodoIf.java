package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

import java.util.LinkedList;

public class NodoIf extends NodoLiteral {
    private NodoLiteral exp;
    private LinkedList<NodoLiteral> sentencias;
    private NodoElse nodoElse = null;
    public static int count = -1;

    // Constructor
    public NodoIf(int line, int col, NodoLiteral exp){
        super(line, col);
        this.exp = exp;
        this.sentencias = new LinkedList<>();
    }

    // Setters
    public void setNodoElse(NodoElse nodoElse) {
        this.nodoElse = nodoElse;
    }

    // Getters
    public int getCount(){
        return count;
    }
    public LinkedList<NodoLiteral> getSentencias() {
        return sentencias;
    }

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }

    @Override
    public String printSentencia(String space) {
        String json = "\"nodo\": \"If\",\n"
                + space + "\"expresion\": {\n"+ this.exp.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"sentencias\":[\n";
        if(!this.sentencias.isEmpty()){
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t"+ space+ this.sentencias.get(i).printSentencia(space+"\t")+space+ "},\n";
            }
            json = json.substring(0,json.length()-2);
        }
        json +="\n" + space + "],\n"
                + space + this.nodoElse.printSentencia(space) + space +"\n";
        return json;
    }


    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoIf: if(exp){sentencias} else
        exp.checkTypes(ts); // Chequeo la expresion
        // Verificar que el exp sea de tipo bool
        if(!(this.exp.getNodeType().equals("Bool"))){
            throw new SemantErrorException(this.exp.getLine(), this.exp.getCol(),
                    "La condicion del if debe ser de tipo Bool",
                    "sentencia");
        }
        // Hacer checkTypes de sus sentencias
        if(!this.getSentencias().isEmpty()) {
            for (NodoLiteral s : this.getSentencias()) { // Recorro las sentencias del bloque
                s.checkTypes(ts);
            }
        }
        // Hacer checkTypes de sus sentenciasElse
        this.nodoElse.checkTypes(ts);
        // Setear el tipo correspondiente una vez que se chequeo
        this.setNodeType(null);
        return true;
    }

    @Override
    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        count =count+1;

        // Generar código para la expresión de la condición (exp)
        code.append(this.exp.generateNodeCode(ts));
        int condReg = CodeGenerator.registerCounter;

        // Generar código para la evaluación de la condición y el salto a la sección else si es falso
        code.append("\t # Condicion\n");
        code.append("\tbeq $t").append(condReg).append(", $zero, ").append("else_"+count).append("\n");
        code.append("\tbne $t").append(condReg).append(", $zero, ").append("if_"+count).append("\n");

        // Generar código para las sentencias del if
        code.append("\t # Sentencias if\n");
        code.append("\tif_" + count).append(":\n");
        for (NodoLiteral sentencia : this.sentencias) {
            code.append(sentencia.generateNodeCode(ts));
        }
        code.append("\tj if_end_" + count).append("\n");

        // Generar la etiqueta y el código para las sentencias del else
        code.append("\telse_" + count).append(":\n");
        code.append("\t # Sentencias else\n");
        if (this.nodoElse != null) {

            code.append(this.nodoElse.generateNodeCode(ts));

        }

        // Generar la etiqueta para el final del if


        return code.toString();
    }

}
