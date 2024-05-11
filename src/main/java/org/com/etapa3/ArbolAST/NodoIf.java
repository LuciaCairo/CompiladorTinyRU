package org.com.etapa3.ArbolAST;

import java.util.LinkedList;

public class NodoIf extends NodoLiteral {

    private NodoLiteral exp;
    private LinkedList<NodoSentencia> sentencias;

    // Constructor
    public NodoIf(int line, int col, NodoLiteral exp){
        super(line, col);
        this.exp = exp;
        this.sentencias = new LinkedList<>();
    }

    // Functions
    public void insertSentencia(NodoSentencia sentencia) {
        this.sentencias.add(sentencia);
    }

    public String printSentencia(String space) {
        return  "\"nodo\": \"If\",\n"
                + space + "\"expresion\": {\n"+ this.exp.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"sentencias\":[]\n";
    }

    /*private NodoExpresion declaracion;
    private LinkedList<NodoSentencia> loop;
    private boolean scoped = false;

    public NodoWhile(int filaTok,int colTok){
        super(filaTok,colTok);
        this.loop = new LinkedList<>();
        this.scoped = false;
    }

    public void setDeclaracion(NodoExpresion declaracion) {
        this.declaracion = declaracion;
    }


    public void addSentencia(NodoSentencia e){
        this.loop.add(e);
    }

    //como es la verificacion del while? como saber cuando cortar? deberia verificarlo yo? o solo verificar que sea todo correcto?
    // verificar condicion y loop si es correcto semanticamente.. es suficiente?

    @Override
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        if(!this.declaracion.checkIsBoolean(ts)){
            throw new ExcepcionSemantica(this.declaracion.getFila(),this.declaracion.getCol(),"No es una declaracion de tipo booleana",this.declaracion.getTipo(ts),false);
        }
        this.loop.forEach((elem)  -> {
            try{
                elem.verifica(ts);
            }catch(ExcepcionSemantica eS){}
        });
        return true;
    }

    public void setScoped(boolean scoped) {
        this.scoped = scoped;
    }

    public boolean isScoped() {
        return scoped;
    }

    public LinkedList<NodoSentencia> getLoop() {
        return loop;
    }


    @Override
    public String imprimeSentencia() {
        String json= "\"nodo\": \"NodoWhile\",\n"
                + "\"declaracion\":{\n"+this.declaracion.imprimeSentencia()+"\n},\n"
                + "\"Bloque\":[";
        for (int i = 0; i < loop.size(); i++) {
            json += "\n{"+ this.loop.get(i).imprimeSentencia() +"},";
        }
        json = json.substring(0,json.length()-1);
        json += "]";


        return json;
    }*/
}
