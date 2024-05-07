package org.com.etapa3.ArbolAST;

public class NodoExpresion extends NodoSentencia{

    private String name;
    private String value;

    public NodoExpresion(int line,int col, String name, String type){
        super(line,col,type);
        this.name = name;
    }

    public NodoExpresion(int line,int col, String name, String type, String value){
        super(line,col,type);
        this.name = name;
        this.value = value;
    }

    public NodoExpresion(int line,int col){
        super(line,col);
    }

/*
    public NodoExpresion(int filaTok,int colTok,String nombre, String tipo){
        super(filaTok,colTok);
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public NodoExpresion(int filaTok,int colTok,String nombre){
        super(filaTok,colTok);
        this.nombre = nombre;
    }

    public String getTipoImpreso(){
        return this.tipo;
    }

    public boolean checkIsBoolean(TablaDeSimbolos ts) throws ExcepcionSemantica{
        return this.tipo.equals("Bool");
    }

    public String getTipo(TablaDeSimbolos ts) throws ExcepcionSemantica {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        return true;
    }

    @Override
    public String imprimeSentencia() {
        String valor = this.valor == null ? "null" : this.valor.toString().replace("\"", "\\\"");
        return "\"nodo\":\"NodoExpresion\",\n\"nombre\":\""+this.nombre+"\",\n\"tipo\":\""+this.tipo+"\",\n\"valor\":\""+valor+"\"";
    }*/



}

