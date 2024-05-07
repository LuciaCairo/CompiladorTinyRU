package org.com.etapa3.ArbolAST;

public class NodoExpBin extends NodoExpresion {
    private NodoExpresion izq;
    private NodoExpresion der;
    private String op;

    // Constructor
    public NodoExpBin(int line,int col, NodoExpresion izq, String op, NodoExpresion der){
        super(line,col);
        this.der = der;
        this.izq = izq;
        this.op = op;
    }

    public NodoExpBin(int line,int col){
        super(line,col);
    }

    // Getters

    // Setters
    public void setNodoD(NodoExpresion der) {
        this.der = der;
    }

    public void setNodoI(NodoExpresion izq) {
        this.izq = izq;
    }

    public void setOp(String op) {
        this.op = op;
    }

    // Functions

    /*
    @Override
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

    public NodoExpresion getIzq() {
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
