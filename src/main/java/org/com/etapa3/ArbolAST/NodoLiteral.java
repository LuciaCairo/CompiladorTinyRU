package org.com.etapa3.ArbolAST;

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
        // Creo que aca no hace falta verificar nada
        // Setear el tipo correspondiente una vez que se chequeo todo, si no tirar error
        return true;
    }
}
