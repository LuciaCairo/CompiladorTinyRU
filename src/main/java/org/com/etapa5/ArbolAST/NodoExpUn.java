package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

// Nodo para expresiones unarias (por ejemplo: ++a, --a, !a)
public class NodoExpUn extends NodoLiteral {

    private String op; // Operador unario y
    private NodoLiteral exp; // Expresión a la que se aplica ese operador

    // Constructores
    public NodoExpUn(int line, int col, String type, NodoLiteral exp, String op){
        super(line,col,type);
        this.exp = exp;
        this.op = op;
    }


    // Functions
    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Expresion Unaria\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"valor\":\""+ this.getNodeType() +"\",\n"
                + space + "\"operador\":\""+ this.op +"\",\n"
                + space + "\"expresion\": {\n"+ this.exp.printSentencia(space+"\t") +"\n"+ space +"}";

    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpUn: opUnario exp
        this.exp.checkTypes(ts);
        // Verificar que exp sea "Bool" cuando el operador es "!"
        if(op.equals("!")){
            if(!this.exp.getNodeType().equals("Bool")){
                throw new SemantErrorException(this.getLine(),this.getCol(),
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" con un " + exp.getNodeType(),
                        "expCompuesta1");
            }
            // Setear el tipo correspondiente
            this.setNodeType("Bool");

        } else { // Verificar que exp sea "Int" en todos los demas casos ++ --
            if(!this.exp.getNodeType().equals("Int")){
                throw new SemantErrorException(this.getLine(),this.getCol(),
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" con un " + exp.getNodeType(),
                        "expCompuesta1");
            }
            // Setear el tipo correspondiente
            this.setNodeType("Int");
        }

        return true;
    }

    @Override
    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        code.append("\n\t# NODO EXPRESION UNARIA \n");

        // Generar código para la expresión
        code.append(this.exp.generateNodeCode(ts));

        // Obtener el registro utilizado por la expresión
        int exprRegister = CodeGenerator.registerCounter;

        switch (op) {
            case "!": // Negación lógica
                int resultRegister = CodeGenerator.getNextRegister();
                code.append("\tli $t").append(resultRegister).append(", 1\n");
                code.append("\txor $t").append(resultRegister).append(", $t").append(exprRegister).append(", $t").append(resultRegister).append("\n");
                break;
            case "++": // Incremento
                code.append("\taddi $t").append(exprRegister).append(", $t").append(exprRegister).append(", 1\n");
                break;
            case "--": // Decremento
                code.append("\taddi $t").append(exprRegister).append(", $t").append(exprRegister).append(", -1\n");
                break;
            case "+": // Operador unario +
                // En MIPS, +x es simplemente x, así que no se necesita generar código adicional.
                break;
            case "-": // Operador unario -
                code.append("\tneg $t").append(exprRegister).append(", $t").append(exprRegister).append("\n");
                break;
        }
        if(this.exp.getClass().getSimpleName().equals("NodoLiteral")){
            // Voy a ver si lo que quiero asignar es una variable
            if (ts.getCurrentMetod().getVariables().containsKey(this.exp.getName())) {
                // Ver caso de que quiera asignar una variable
                // Ejemplo a = 1 (que a sea variable del metodo)

            } // Si no, voy a ver si lo que quiero asignarr es un parametro
            else if (ts.getCurrentMetod().getParametros().containsKey(this.exp.getName())) {
                // Ver caso de que quiera asignar un parametro
                // Ejemplo a = 1 (que a sea parametro del metodo )

            } // Si no, voy a ver si lo que quiero modificar es un atributo
            else if (ts.getCurrentStruct().getAtributos().containsKey(this.exp.getName())) {
                code.append("\t # Guardo el valor en el atributo\n");

                int offset = ts.getCurrentStruct().getAtributos().get(exp.getName()).getPos() * 4 +4; //AGREGA AGUS
                code.append("\tsw $t" + exprRegister + ", " + offset + "($s1)"+"\n");

            }
        }

        //CodeGenerator.getNextRegister();
        return code.toString();
    }
}
