package org.com.etapa3.ArbolAST;

public class NodoLiteral extends NodoSentencia{

    private String name;
    private String value = null;

    public NodoLiteral(int line, int col, String name, String type){
        super(line,col,type);
        this.name = name;
    }

    public NodoLiteral(int line, int col, String name, String type, String value){
        super(line,col,type);
        this.name = name;
        this.value = value;
    }

    public NodoLiteral(int line, int col){
        super(line,col);
    }

    // Getters
    public String getName() {
        return name;
    }

/*
    public NodoLiteral(int filaTok,int colTok,String nombre, String tipo){
        super(filaTok,colTok);
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public NodoLiteral(int filaTok,int colTok,String nombre){
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
        return "\"nodo\":\"NodoLiteral\",\n\"nombre\":\""+this.nombre+"\",\n\"tipo\":\""+this.tipo+"\",\n\"valor\":\""+valor+"\"";
    }*/
@Override
public String printSentencia(String space) {
    return space + "\"nodo\": \"Literal\",\n"
            + space + "\"nombre\":\""+ this.name +"\",\n"
            + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
            + space + "\"valor\":\""+ this.value +"\"";
}


}

