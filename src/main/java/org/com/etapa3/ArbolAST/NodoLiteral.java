package org.com.etapa3.ArbolAST;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

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

        // IGNORAMOS LOS LITERALES(INT,STR...) PORQUE YA TIENEN TIPO Y VALOR
        if(!(this.getName().equals("literal nulo") ||
                this.getName().equals("literal bool")||
                this.getName().equals("literal entero")||
                this.getName().equals("literal str")||
                this.getName().equals("punto y coma")||
                this.getName().equals("literal char"))){

            // CASO ESPECIAL DE START
            if(ts.getCurrentStruct().getName().equals("start")){

                // SOLO VEMOS SI EL ID ESTA DECLARADO COMO VARIABLE DEL START
                if(!(ts.getCurrentStruct().getVariables().containsKey(this.getName()))){

                    // SI EL ID NO ESTA DECLARADO, ERROR
                    throw new SemantErrorException(this.getLine(),
                                    this.getCol(), "El id \"" + this.getName() +
                                    "\" no esta declarado en el struct '"+ts.getCurrentStruct().getName()+"'.", "");
                }else{
                    this.setNodeType(ts.getCurrentStruct().getVariables().get(this.getName()).getType());
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
        return true;
    }
}
