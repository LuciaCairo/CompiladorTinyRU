package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;

import java.util.LinkedList;

public class NodoLlamadaMetodo extends NodoLiteral{
    private String typeStruct;
    private String nameStruct;
    private String metodo;
    private LinkedList<NodoLiteral> argumentos;


    public NodoLlamadaMetodo(int line, int col, String nameStruct,String typeStruct, String metodo, String type){
        super(line, col, type);
        this.nameStruct = nameStruct;
        this.typeStruct = typeStruct;
        this.metodo = metodo;
        this.argumentos = new LinkedList<>();
    }

    // Getters

    public String getTypeStruct() {
        return typeStruct;
    }

    public String getMetodo() {
        return metodo;
    }
    // Setters

    // Functions
    public void insertArgumento(NodoLiteral argumento) {
        this.argumentos.add(argumento);
    }

    @Override
    public String printSentencia(String space) {
        String json = space + "\"nodo\": \"Llamada Metodo\",\n"
                + space + "\"metodo\": {\n"+ this.metodo +"\n" + space +"},\n";
        if (!this.argumentos.isEmpty() && !(this.argumentos.getFirst() == null) ) {
            json +=  space + "\"argumentos\":[\n";
            for (int i = 0; i < this.argumentos.size(); i++) {
                json += space + "{\n\t" + space + this.argumentos.get(i).printSentencia(space + "\t") + space + "},\n";
            }
            json = json.substring(0, json.length() - 2);
        } else {
            json +=  space + "\"argumentos\":[]\n";
        }
        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoLlamadaMetodo: metodo(lista expresiones)
        // Verificar que el metodo exista en su structs padre en la ts
        // Verificar los argumentos de la llamada coincidan en tipo y cantidad con los argumentos que espera el metodo
        return true;
    }

}
