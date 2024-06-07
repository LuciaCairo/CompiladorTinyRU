package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

public class NodoLiteral extends Nodo{

    public NodoLiteral(int line, int col){
        super(line, col);
    }
    public NodoLiteral(int line, int col, String type){
        super(line, col, type);
    }
    public NodoLiteral(int line, int col, String type, String value){
        super(line, col, type,value);
    }
    public NodoLiteral(int line, int col, String name, String type, String value){
        super(line, col, name, type,value);
    }

    public String printSentencia(String space) {
        return space + "\"nodo\": \"Literal\",\n"
                + space + "\"nombre\":\""+ this.getName() +"\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"valor\":\""+ this.getValue() +"\"";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){

        // Caso para llamada de metodos estaticos
        if(this.getValue()!= null){
            if(this.getValue().equals("st") && (ts.getTableStructs().containsKey(this.getName()))){
                return true;
            }
        }
        // IGNORAMOS LOS LITERALES(INT,STR...) PORQUE YA TIENEN TIPO Y VALOR
        if(!(this.getName().equals("literal nulo") ||
                this.getName().equals("literal bool")||
                this.getName().equals("literal entero")||
                this.getName().equals("literal str")||
                this.getName().equals("punto y coma")||
                this.getName().equals("self") ||
                this.getName().equals("IO") ||
                this.getName().equals("literal char"))){

            // CASO ESPECIAL DE START
            if(ts.getCurrentStruct().getName().equals("start")){

                if(!this.getParent().isEmpty()){ // Viene de un acceso
                    // VEMOS SI EL ID ESTA DECLARADO COMO ATRIBUTO DEL STRUCT DEL QUE SE ACCEDE
                    if (!(ts.getTableStructs().get(this.getParent()).getAtributos().containsKey(this.getName()))) {
                        // SI EL ID NO ESTA DECLARADO, ERROR
                        throw new SemantErrorException(this.getLine(),
                                this.getCol(), "El id \"" + this.getName() +
                                "\" no esta declarado como atributo del struct '" + this.getParent() + "'.", "encadenadoSimple");
                    } else {
                        this.setNodeType((ts.getTableStructs().get(this.getParent()).getAtributos().get(this.getName())).getType());
                    }
                } else {
                    // SOLO VEMOS SI EL ID ESTA DECLARADO COMO VARIABLE DEL START
                    if (!(ts.getCurrentStruct().getVariables().containsKey(this.getName()))) {

                        // SI EL ID NO ESTA DECLARADO, ERROR
                        throw new SemantErrorException(this.getLine(),
                                this.getCol(), "El id \"" + this.getName() +
                                "\" no esta declarado en el struct '" + ts.getCurrentStruct().getName() + "'.", "");
                    } else {
                        this.setNodeType(ts.getCurrentStruct().getVariables().get(this.getName()).getType());
                    }
                }

            } else { // CASO DE TODOS LOS DEMAS STRUCTS

                if(this.getParent().isEmpty()){
                    // VEMOS SI EL ID ESTA DECLARADO COMO VARIABLE DEL METODO

                    if(!(ts.getCurrentMetod().getVariables().containsKey(this.getName()))){

                        // SI NO, VEMOS SI EL ID ESTA DECLARADO COMO PARAMETRO DEL METODO
                        if (!(ts.getCurrentMetod().getParametros().containsKey(this.getName()))){

                            // SI NO, VEMOS SI EL ID ESTA DECLARADO COMO ATRIBUTO DEL STRUCT
                            if(!(ts.getCurrentStruct().getAtributos().containsKey(this.getName()))){

                                // SI EL ID NO ESTA DECLARADO EN NINGUN LUGAR, ERROR
                                throw new SemantErrorException(this.getLine(),
                                        this.getCol(), "El id \"" + this.getName() +
                                        "\" no esta declarado en el struct '"+ts.getCurrentStruct().getName()+"' ni en el metodo '"+
                                        ts.getCurrentMetod().getName()+ "'", "encadenadoSimple");
                            }else{
                                this.setNodeType((ts.getCurrentStruct().getAtributos().get(this.getName())).getType());
                            }
                        }else{
                            this.setNodeType((ts.getCurrentMetod().getParametros().get(this.getName())).getType());
                        }
                    }else{
                        this.setNodeType((ts.getCurrentMetod().getVariables().get(this.getName())).getType());
                    }
                } else {

                    // VEMOS SI EL ID ESTA DECLARADO COMO ATRIBUTO DEL STRUCT
                    if (!(ts.getTableStructs().get(this.getParent()).getAtributos().containsKey(this.getName()))) {
                        // SI EL ID NO ESTA DECLARADO, ERROR
                        throw new SemantErrorException(this.getLine(),
                                this.getCol(), "El id \"" + this.getName() +
                                "\" no esta declarado como atributo del struct '" + this.getParent() + "'.", "encadenadoSimple");
                    } else {
                        this.setNodeType((ts.getTableStructs().get(this.getParent()).getAtributos().get(this.getName())).getType());
                    }
                }
            }
        }
        // No puede haber self en el start
        if(this.getName().equals("self") && ts.getCurrentStruct().getName().equals("start")){
            throw new SemantErrorException(this.getLine(), this.getCol(),
                    "No se puede realizar un self en start", "encadenadoSimple");
        }
        // No puede haber self en el metodo st
        if(this.getName().equals("self") && ts.getCurrentMetod().getSt()){
            throw new SemantErrorException(this.getLine(), this.getCol(),
                    "No se puede realizar un self en un metodo estatico", "encadenadoSimple");
        }
        return true;
    }

    // Método para generar el código MIPS para un nodo literal
    @Override
    public String generateNodeCode(TablaSimbolos ts) {
        String code = "";
        String value = this.getValue();
        String n = this.getName();
        if (n.equals("literal entero")) {
            code = "li $t" + CodeGenerator.registerCounter + ", " + value + "\n";
        } else if (n.equals("literal str")) {
            // Los literales de cadena pueden necesitar una etiqueta en la sección .data
            String label = "str" + CodeGenerator.registerCounter;
            code = ".data\n" + label + ": .asciiz \"" + value + "\"\n";
            code += ".text\nla $t" + CodeGenerator.registerCounter+ ", " + label + "\n";
        } else if (n.equals("literal char")) {
            int charValue = (int) value.charAt(0); // Obtiene el valor ASCII del caracter
            code = "li $t" + CodeGenerator.registerCounter + ", " + charValue + "\n";
        } else if (n.equals("literal bool")) {
            int boolValue = value.equals("true") ? 1 : 0;
            code = "li $t" + CodeGenerator.registerCounter + ", " + boolValue + "\n";
        } else if (n.equals("nil")) {
            code = "li $t" + CodeGenerator.registerCounter + ", 0\n";
        } else {
            if(ts.getCurrentStruct().getName().equals("start")){
                int offset = ts.getCurrentStruct().getVariables().get(this.getName()).getPos() * 4;
                code = "lw $t" + CodeGenerator.registerCounter + ", " + offset + "($sp)\n";
            } else {
                if ((ts.getCurrentMetod().getParametros().containsKey(this.getName()))) {
                    code = "move $t" + CodeGenerator.registerCounter + ", $a" + ts.getCurrentMetod().getParametros().get(this.getName()).getPos() +
                            "  # Guarda parametro en un registro temporal\n";
                } else if(ts.getCurrentStruct().getVariables().containsKey(this.getName())) {
                    int offset = ts.getCurrentStruct().getVariables().get(this.getName()).getPos() * 4;
                    code = "lw $t" + CodeGenerator.registerCounter + ", " + offset + "($fp)\n";
                }
                else {
                    int offset = ts.getCurrentStruct().getAtributos().get(this.getName()).getPos() * 4;
                    code = "lw $t" + CodeGenerator.registerCounter + ", " + offset + "($sp)\n";
                }
            }
        }
        CodeGenerator.getNextRegister();
        return code;
    }

}
