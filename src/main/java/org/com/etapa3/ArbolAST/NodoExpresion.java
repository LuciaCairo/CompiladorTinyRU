package org.com.etapa3.ArbolAST;

// Este es el nodo con la estructura general del que heredan todos los nodos
public class NodoExpresion extends NodoLiteral{

    private NodoLiteral exp;

    // Constructor
    public NodoExpresion(int line, int col, String name, String type, String value, NodoLiteral exp){
        super(line, col,name,type, value);
        this.exp = exp;
    }

    // Functions

    public String printSentencia(String space) {
        if (this.getName().equals("Retorno") && this.exp.getNodeType().equals("void")) {
            return "";

        } else {
            return "\"nodo\": \"" + this.getName() + "\",\n"
                    + space + "\"tipo\":\"" + this.getNodeType() + "\",\n"
                    + space + "\"expresion\": {\n" + this.exp.printSentencia(space + "\t") + "\n" + space + "},\n";

        }
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
