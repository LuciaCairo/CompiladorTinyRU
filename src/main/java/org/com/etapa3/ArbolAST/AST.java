package org.com.etapa3.ArbolAST;

import org.com.etapa3.SyntactErrorException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AST {
    private HashMap<String,NodoStruct> structs;
    private NodoStruct currentStruct;
    private NodoMetodo currentMetodo;
    private Stack<Nodo> profundidad;

    // Constructor
    public AST(){
        this.structs = new HashMap<>();
        this.profundidad = new Stack<>();
    }

    // Getters
    public NodoStruct getCurrentStruct() {
        return currentStruct;
    }
    public NodoMetodo getCurrentMetodo() {
        return currentMetodo;
    }
    public Stack<Nodo> getProfundidad() {
        return profundidad;
    }


    // Setters
    public void setCurrentStruct(NodoStruct currentStruct) {
        this.currentStruct = currentStruct;
    }
    public void setCurrentMetodo(NodoMetodo currentMetodo) {
        this.currentMetodo = currentMetodo;
    }

    // Functions
    public void insertStruct(String name, NodoStruct nodo) {
        this.structs.put(name, nodo);
    }
    public String checkTypes(Nodo nodo1 , Nodo nodo2){
        String n1= nodo1.getNodeType();
        String n2= nodo2.getNodeType();
        if(n1.equals(n2)){
            return (nodo1.getNodeType());
        }
        return null;
    }

    public String printJSON_Arbol(String input){
        String json = "{\n";
        json += "\"nombre\": \"AST-"+input+"\",\n"; // "AST-ejemplo.ru",
        json += "\"structs\": [\n";
        for(Map.Entry<String, NodoStruct> entry : structs.entrySet()) {
            NodoStruct value = entry.getValue();
            json +="{\n\t\"nombreImpl\": \""+ value.getName() + "\",\n"+ value.printNodoStruct()+"\n},\n";
        }
        json = json.substring(0,json.length()-2);
        json += "\n]\n}";
        return json;
    }

    // Método para guardar el JSON en un archivo
    public void saveJSON(String json, String nombreArchivo) {
        //String rutaActual = System.getProperty("user.dir");
        //String rutaArchivo = rutaActual + "\\" + nombreArchivo;
        String rutaArchivo = System.getProperty("user.dir") + "\\src\\main\\java\\org\\com\\etapa3\\" + nombreArchivo;
        try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
            fileWriter.write(json);
            System.out.println("JSON guardado exitosamente en " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar el JSON");
            e.printStackTrace();
        }
    }

    /*




    public NodoAST popScope() {
        return this.scope.pop();
    }

    public void pushScope(NodoAST scope) {
        this.scope.push(scope);
    }

    public NodoAST peekScope(){
        return this.scope.peek();
    }

    public Stack<NodoAST> getScope() {
        return scope;
    }




    public boolean verifica(TablaDeSimbolos ts)throws ExcepcionSemantica{
        for (int i = 0; i < scope.size(); i++) {
            NodoClase nC = (NodoClase) scope.get(i);
            nC.verifica(ts);
        }
        return true;
    }*/

}

