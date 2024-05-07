package org.com.etapa3.ArbolAST;

public class NodoSentencia {
    private int line, col;
    public NodoSentencia(int line,int col){
        this.line = line;
        this.col = col;
    }

    /*
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica{
        return true;
    }

    public String imprimeSentencia(){
        return "\"nodo\": \"Sentencia\"";
    }*/
}
