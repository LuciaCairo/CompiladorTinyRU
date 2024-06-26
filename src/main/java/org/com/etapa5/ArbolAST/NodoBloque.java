package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;
import java.util.LinkedList;

public class NodoBloque extends NodoLiteral{

    private LinkedList<NodoLiteral> sentencias;

    // Constructor
    public NodoBloque(int line, int col){
        super(line, col);
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
        String json = "\"nodo\": \"Bloque\",\n"
                + space + "\"sentencias\":[\n";
        if(!this.sentencias.isEmpty()){
            for (int i = 0; i < this.sentencias.size(); i++) {
                json += space + "{\n\t"+ space+ this.sentencias.get(i).printSentencia(space+"\t")+space+ "},\n";
            }
            json = json.substring(0,json.length()-2);
        }
        json +="\n" + space + "]\n";
        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoBloque: Bloque {sentencias}
        // Chequeo de sentencias
        if(!this.getSentencias().isEmpty()) {
            for (NodoLiteral s : this.getSentencias()) { // Recorro las sentencias del bloque
                s.checkTypes(ts);
            }
        }
        this.setNodeType(null);
        return true;
    }

    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();

        if(!this.getSentencias().isEmpty()) {
            for (NodoLiteral s : this.getSentencias()) { // Recorro las sentencias del bloque
                code.append(s.generateNodeCode(ts));
            }
        }

        return code.toString();
    }
}
