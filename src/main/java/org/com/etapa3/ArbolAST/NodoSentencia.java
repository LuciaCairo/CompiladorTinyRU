package org.com.etapa3.ArbolAST;

public class NodoSentencia extends Nodo{

    public NodoSentencia(int line,int col){
        super(line, col);
    }
    public NodoSentencia(int line,int col, String type){
        super(line, col, type);
    }

    public String printSentencia(String space){
        return "\"nodo\": \"Sentencia\"" ;
    }
    /*
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica{
        return true;
    }

    */
}
