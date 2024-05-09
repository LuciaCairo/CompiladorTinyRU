package org.com.etapa3.ArbolAST;

// Nodo para expresiones unarias (por ejemplo: ++a, --a, !a, etc)
public class NodoExpUn extends NodoLiteral {

    private String op; // Operador unario y
    private NodoLiteral exp; // Expresión a la que se aplica ese operador

    // Constructores
    public NodoExpUn(int line, int col, Nodo parent, NodoLiteral exp, String op){
        super(line,col);
        this.exp = exp;
        this.op = op;
    }

    public NodoExpUn(int line,int col){
        super(line,col);
    }

    // Getters
    public String getOp() {
        return op;
    }
    public NodoLiteral getExp() {
        return exp;
    }

    // Setters
    public void setExp(NodoLiteral exp) {
        this.exp = exp;
    }

    public void setOp(String op) {
        this.op = op;
    }


    /*

    @Override
    public boolean checkIsBoolean(TablaDeSimbolos ts) throws ExcepcionSemantica  {
        return (this.oper.equals("!") && der.getTipo(ts).equals("Bool"));
    }


    @Override
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        String derT = der.getTipo(ts);
        if(this.oper.equals("!") ){

            if(derT.equals("Bool")){
                return true;
            }else{
                throw new ExcepcionSemantica(super.getFila(),super.getCol(),"La expresion contiene tipos incompatibles","operador: "+this.oper+" y tipo: "+derT,false);
            }
        }else{
            if(oper.equals("-") && oper.equals("+") ){
                if(derT.equals("Int")){
                    return true;
                }else{
                    throw new ExcepcionSemantica(super.getFila(),super.getCol(),"La expresion contiene tipos incompatibles","operador: "+this.oper+" y tipo: "+derT,false);

                }
            }
        }
        return false;
    }



    @Override
    public String getTipo(TablaDeSimbolos ts) throws ExcepcionSemantica {
        return this.der.getTipo(ts);
    }

    @Override
    public String imprimeSentencia() {
        return "\"nodo\": \"NodoExpresionUnaria\",\n"
                + "\"operador\":\""+this.oper+"\",\n"
                + "\"ladoDer\":{"+this.der.imprimeSentencia()+"\n}";
    }

     */
}
