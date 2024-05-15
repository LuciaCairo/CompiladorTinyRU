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

        // Setear el tipo correspondiente una vez que se chequeo todo, si no tirar error
        String[] palabras = (this.getName().split(" "));
        String isArray = palabras[0];
        //IGNORAMOS LOS LITERALES(INT,STR...) PORQUE YA TIENEN TIPO Y VALOR
        if(!(this.getName().equals("literal nulo") ||
                this.getName().equals("literal bool")||
                this.getName().equals("literal entero")||
                this.getName().equals("literal str")||
                this.getName().equals("literal char")||
                isArray.equals("Array"))){
            //VEMOS SI EL ID ESTA DECLARADO COMO VARIABLE DEL METODO
            if(!(ts.getCurrentMetod().getVariables().containsKey(this.getName()))){
                if (!(ts.getCurrentMetod().getParametros().containsKey(this.getName()))){
                    if(!(ts.getCurrentStruct().getAtributos().containsKey(this.getName()))){
                        throw new SemantErrorException(this.getLine(),
                                this.getCol(), "El id \"" + this.getName() +
                                "\" no esta declarado ni en el struct '"+ts.getCurrentStruct().getName()+"' ni en el metodo '"+
                                ts.getCurrentMetod().getName()+ "'", "encadenadoSimple");
                    }else{
                        System.out.println(ts.getCurrentStruct().getAtributos().get(this.getName()).getName());
                        this.setNodeType(ts.getCurrentStruct().getAtributos().get(this.getName()).getType());
                    }
                }else{
                    this.setNodeType(ts.getCurrentMetod().getParametros().get(this.getName()).getType());
                }

            }else{
                this.setNodeType(ts.getCurrentMetod().getVariables().get(this.getName()).getType());
            }
            /*if(!(ts.getTableStructs().containsKey(this.getNodeType()))){
                throw new SemantErrorException(this.getLine(),
                        this.getCol(), "El struct \"" + this.getNodeType() +
                        "\" no existe", "encadenadoSimple");
            }*/

        }

        return true;
    }
}
