package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

public class NodoExpBin extends NodoLiteral {
    private NodoLiteral izq;
    private NodoLiteral der;
    private String op;

    // Constructor
    public NodoExpBin(int line, int col, NodoLiteral izq, String op, NodoLiteral der, String type){
        super(line,col,type);
        this.der = der;
        this.izq = izq;
        this.op = op;
    }

    public NodoExpBin(int line,int col){
        super(line,col);
    }

    // Getters

    // Setters
    public void setNodoD(NodoLiteral der) {
        this.der = der;
    }
    public void setNodoI(NodoLiteral izq) {
        this.izq = izq;
    }
    public void setOp(String op) {
        this.op = op;
    }

    // Functions
    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Expresion Binaria\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"valor\":\""+ this.getValue() +"\",\n"
                + space + "\"operador\":\""+ this.op +"\",\n"
                + space + "\"nodoIzq\": {\n"+ this.izq.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"nodoDer\": {\n"+ this.der.printSentencia(space+"\t") +"\n" + space +"}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpBin: nodoI op nodoD
        // Verificar que el nodoI y el nodoD sean de tipo "Int"
        return true;
    }

    /*

    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {

        if(this.oper.equals("||") || this.oper.equals("&&")){
            if(der.getTipo(ts).equals(izq.getTipo(ts)) && der.getTipo(ts).equals("Bool")){
                return true;
            }else{
                String comp = izq+ " y "+der;
                throw new ExcepcionSemantica(this.getFila(),this.getCol(),"La expresion contiene tipos incompatibles",comp,false);
            }
        }else{
            if(this.oper.equals("*") && this.oper.equals("/") && this.oper.equals("%") && this.oper.equals("-") && this.oper.equals("<") && this.oper.equals(">") && this.oper.equals("<=") && this.oper.equals(">=") && this.oper.equals("==") ){
                if(der.getTipo(ts).equals(izq.getTipo(ts)) && der.getTipo(ts).equals("Int")){
                    return true;
                }else{
                    String comp = izq+ " y "+der;
                    throw new ExcepcionSemantica(this.getFila(),this.getCol(),"La expresion contiene tipos incompatibles",comp,false);

                }
            }else{
                if(this.oper.equals("+")){
                    if(der.getTipo(ts).equals(izq.getTipo(ts)) && (der.getTipo(ts).equals("Int") || der.getTipo(ts).equals("String") || der.getTipo(ts).equals("Char"))){
                        return true;
                    }else{
                        String comp = izq+ " y "+der;
                        throw new ExcepcionSemantica(this.getFila(),this.getCol(),"La expresion contiene tipos incompatibles",comp,false);
                    }
                }
            }
        }
        System.out.println("tipo no manejado:: "+this.oper);
        return false;
    }

    public String getOper() {
        return oper;
    }



    public void setOper(String oper) {
        this.oper = oper;
    }

    public NodoLiteral getIzq() {
        return izq;
    }

    @Override
    public String getTipo(TablaDeSimbolos ts) throws ExcepcionSemantica {
        if(this.oper.equals("<") || this.oper.equals(">") || this.oper.equals("<=") || this.oper.equals(">=") || this.oper.equals("==") || this.oper.equals("!=")){
            if(der.getTipo(ts).equals(izq.getTipo(ts)) && der.getTipo(ts).equals("Int")){
                return "Bool";
            }
        }
        return der.getTipo(ts);
    }

    @Override
    public boolean checkIsBoolean(TablaDeSimbolos ts) throws ExcepcionSemantica  {
        return (this.getTipo(ts).equals("Bool"));
    }



    @Override
    public String imprimeSentencia() {
        return "\"nodo\": \"NodoExpresionBinaria\",\n"
                + "\"ladoIzq\":{\n"+this.izq.imprimeSentencia()+"\n},\n"
                + "\"operador\":\""+this.oper+"\",\n"
                + "\"ladoDer\":{"+this.der.imprimeSentencia()+"\n}";
    }

*/

}
