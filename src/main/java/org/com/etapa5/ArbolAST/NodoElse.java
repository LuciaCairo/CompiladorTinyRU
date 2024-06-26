package org.com.etapa5.ArbolAST;

import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

import java.util.LinkedList;

public class NodoElse extends NodoLiteral {
    private LinkedList<NodoLiteral> sentencias;
    private NodoIf nodoIf;
    public static int count = -1;

    // Constructor
    public NodoElse(int line, int col, NodoIf nodoIf){
        super(line, col);
        this.nodoIf = nodoIf;
        this.sentencias = new LinkedList<>();
    }

    // Getters


    public LinkedList<NodoLiteral> getSentencias() {
        return sentencias;
    }

    // Functions
    public void insertSentencia(NodoLiteral sentencia) {
        this.sentencias.add(sentencia);
    }

    @Override
    public String printSentencia(String space) {
        String json =  "\"sentenciasElse \":[\n";
        if(!this.sentencias.isEmpty()){
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t"+ space+ this.sentencias.get(i).printSentencia(space+"\t")+space+ "},\n";
            }
            json = json.substring(0,json.length()-2);
            json +="\n" + space;
            json +="]\n";
        } else{
            json = json.substring(0,json.length()-1);
            json +="]\n";
        }

        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoElse: else {sentencias}
        // Chequeo de sentencias
        if(!this.getSentencias().isEmpty()) {
            for (NodoLiteral s : this.getSentencias()) { // Recorro las sentencias del else
                s.checkTypes(ts);
            }
        }
        this.setNodeType(null);
        return true;
    }

    @Override
    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        int countR= nodoIf.getCount();
        // Generar código para las sentencias del else
        for (NodoLiteral sentencia : this.sentencias) {
            code.append(sentencia.generateNodeCode(ts));
        }

        code.append("\tj if_end_" + countR).append("\n");
        code.append("\tif_end_" + countR).append(":\n");
        return code.toString();
    }

}
