package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

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

    // Funcion para generar el codigo en MIPS de una asignacion
    @Override
    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        code.append("\n\t# NODO EXPRESION BINARIA \n");
        // Generar código para el nodo izquierdo
        code.append(this.nodoI.generateNodeCode(ts));
        int leftRegister = CodeGenerator.getBefRegister();

        // Generar código para el nodo derecho
        code.append(this.nodoD.generateNodeCode(ts));
        int rightRegister = CodeGenerator.getBefRegister();

        int resultRegister = CodeGenerator.getNextRegister();

        switch (op) {
            case "+":
                code.append("\tadd $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "-":
                code.append("\tsub $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "*":
                code.append("\tmul $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "/":
                code.append("\tdiv $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "||":
                code.append("\tor $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "&&":
                code.append("\tand $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "==":
                code.append("\tseq $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "!=":
                code.append("\tsne $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "<":
                code.append("\tslt $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case ">":
                code.append("\tsgt $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case "<=":
                code.append("\tsle $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            case ">=":
                code.append("\tsge $t").append(resultRegister).append(", $t").append(leftRegister).append(", $t").append(rightRegister).append("\n");
                break;
            default:
                System.out.println("SE ESCAPO UN CASO NODOEXPBIN " + op);
        }
        return code.toString();
    }
}
