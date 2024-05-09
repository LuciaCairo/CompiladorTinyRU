package org.com.etapa3.ArbolAST;

// Nodo para las asignaciones (expresion = expresion)
public class NodoAcceso extends NodoLiteral {

    private NodoLiteral izq;
    private NodoLiteral der;


    // Constructores
    public NodoAcceso(int line, int col, NodoLiteral izq, NodoLiteral der){
        super(line, col);
        this.der = der;
        this.izq = izq;
    }

    @Override
    public String printSentencia(String space) {
        return space + "\"nodo\": \"Acceso\",\n"
                + space + "\"nodoIzq\": {\n"+ this.izq.printSentencia(space+"\t") +"\n" + space +"},\n"
                + space + "\"nodoDer\": {\n"+ this.der.printSentencia(space+"\t") +"\n" + space +"}";
    }

    /*



    public NodoAsignacion(int filaTok,int colTok,NodoLiteral izqui,String tipo){
        super(filaTok,colTok);
        this.izq = izqui;
        this.tipoAsig = tipo;
    }

    public void setDer(NodoLiteral der) {
        this.der = der;
    }

    public void setIzq(NodoLiteral izq) {
        this.izq = izq;
    }

    @Override
    public String getTipo(TablaDeSimbolos ts) throws ExcepcionSemantica {
        return this.izq.getTipo(ts);
    }

    public String getTipoAsig() {
        return tipoAsig;
    }

    public NodoLiteral getIzq() {
        return izq;
    }

    public NodoLiteral getDer() {
        return der;
    }



    @Override
    public boolean verifica(TablaDeSimbolos ts) throws ExcepcionSemantica {
        //TODO verificar herencias y compatibilidades
        //TODO if tipoAsig es != primitivo throw err
        if(izq.getTipo(ts).equals(der.getTipo(ts)) || der.getTipo(ts).equals("nil")){
            return true;
        }else{

            String comp = this.izq.getTipo(ts)+ " y "+this.der.getTipo(ts);
            throw new ExcepcionSemantica(this.getFila(),this.getCol(),"La asignacion contiene tipos incompatibles",comp,false);
        }
    }


}


    @Override
    public String printSentencia(String space) {
        return "\"nodo\": \"Acceso\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"NodoIzq\":{\n"+ this.izq.printSentencia(space+"\t")+"\n"+ space +"},\n"
                + space + "\"NodoDer\":{\n"+ this.der.printSentencia(space+"\t")+ space +"\n" + space + "}\n";
    }*/

}