package org.com.etapa3.ArbolAST;

import java.util.LinkedList;

public class NodoMetodo {
    private String name;
    private int line, col;
    private LinkedList<NodoSentencia> sentencias;
    private String nameFather;

    public NodoMetodo(int line,int col,String name, String nameFather){
        this.line = line;
        this.col = col;
        this.name = name;
        this.nameFather = nameFather;
        this.sentencias = new LinkedList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    // Setters

    // Functions
    public String printNodoMet(){
        String json = "";
        json += "\t\t\t\t\"sentencias\":[\n";

        json +="\t\t\t\t]\n";
        return json;
    }

    public void insertSentencia(NodoSentencia sentencia) {
        this.sentencias.add(sentencia);
    }

    /*



    public LinkedList<NodoSentencia> getBloque() {
        return bloque;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRetorno(NodoExpresion retorno) {
        this.retorno = retorno;
    }



    public LinkedList<NodoExpresion> getArgs() {
        return args;
    }

    public void putArg(NodoExpresion nE){
        this.args.add(nE);
    }

    @Override
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        EntradaMetodo eC = ts.getClases().get(this.padre).getMetodo(this.nombre);

        if(this.retorno!=null && !this.retorno.getTipo(ts).equals(eC.getTipoRetorno())){
            throw new ExcepcionSemantica(this.getFila(),this.getCol(),"El tipo de retorno no coincide con el declarado",this.nombre,false);
        }
        if(!this.bloque.isEmpty()){
            for (int i = 0; i < this.bloque.size(); i++) {
                this.bloque.get(i).verifica(ts);
            }
        }
        return true;
    }

    public String getPadre() {
        return padre;
    }

    public NodoExpresion getRetorno() {
        return retorno;
    }

    public String getTipo() {
        return tipo;
    }



  */

}
