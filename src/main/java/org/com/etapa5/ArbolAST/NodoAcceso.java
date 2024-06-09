package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

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
        this.nodoD.setParent(nodoI.getNodeType());

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
        } else if (this.nodoI.getNodeType().equals("IO") || this.nodoI.getNodeType().equals("Str")) {
            if (this.nodoD.getClass().getSimpleName().equals("NodoLiteral")) {
                throw new SemantErrorException(this.nodoD.getLine(), this.nodoD.getCol(),
                        "Acceso incorrecto. No se puede acceder al atributo ya que no existe en el struct \"" + this.nodoI.getNodeType() + "\"",
                        "NodoAcceso");
            } else {
                // Chequeo del nodoD
                this.nodoD.checkTypes(ts);
            }
        } else {
            if (this.nodoI.getName() == null) { // cuando entras a funciones predefinidas que no tienen metodos ejemplo in_int
                throw new SemantErrorException(this.nodoI.getLine(), this.nodoI.getCol(),
                        "Acceso Incorrecto."+"\"" + this.nodoI.getNodeType() + "\" no es de tipo Struct por lo que no se puede usar usar para un acceso",
                        "NodoAcceso");
            }
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

    // Funcion para generar el codigo en MIPS de una asignacion
    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        code.append("\n\t# NODO ACCESO \n");
        if(ts.getStructsPred().containsKey(this.nodoI.getName())){
            code.append(this.nodoD.generateNodeCode(ts));
            return code.toString();
        }

        if(this.nodoI.getClass().getSimpleName().equals("NodoLiteral")){
            // El nodo izquierdo es un struct, entonces yo quiero acceder a su instancia
            int posInst; // fib.j
            if(ts.getCurrentStruct().getName().equals("start")) {
                posInst = ts.getCurrentStruct().getVariables().get(this.nodoI.getName()).getPos() * 4 + 4;

            } else {
                posInst = 0;
            }
            int reg1=CodeGenerator.getNextRegister();
            code.append("\t # Cargo la instancia " + nodoI.getName() + "\n");
            code.append("\tlw $t" + reg1 +", -" + posInst+"($fp) #carga en $t... la direccion de la instancia\n");

            if(this.nodoD.getClass().getSimpleName().equals("NodoLiteral")){
                // El nodo derecho es un atributo
                code.append("\t # Accedo al atributo " + nodoD.getName() + "\n");
                int reg = CodeGenerator.getBefRegister();
                int posAtr = ts.getStruct(this.nodoI.getNodeType()).getAtributo(this.nodoD.getName()).getPos()*4 +4;
                code.append("\tlw $t" + reg1 +"," + posAtr +" ($t"+ reg + ")\n");
            }else if (this.nodoD.getClass().getSimpleName().equals("NodoLlamadaMetodo")){ //

                // El nodo derecho es una llamada metodo
                code.append("\t # Accedo al metodo "  + "\n"); //al metodo incrementador
                int reg = CodeGenerator.getBefRegister();
                code.append("\tmove $a0, $t" +reg+" # mueve a a0 la direccion de la instancia \n"
                            +"\tlw $t"+CodeGenerator.getNextRegister()+", 0($a0) #cargo en $t... la direccion a la vtable\n");
                NodoLlamadaMetodo name = (NodoLlamadaMetodo) this.nodoD;
                String name1= name.getMetodo();
                int posMet = ts.getStruct(this.nodoI.getNodeType()).getMetodo(name1).getPos()*4;
                reg = CodeGenerator.getBefRegister();
                code.append("\tlw $s0"+", "+posMet+"($t"+reg+") # Cargar el puntero al método desde la vtable\n");
                code.append(this.nodoD.generateNodeCode(ts));
            }
            return code.toString();
        }
        return code.toString();
    }
}