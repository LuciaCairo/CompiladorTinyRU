package org.com.etapa3;
import org.com.etapa3.ClasesSemantico.EntradaStruct;
import org.com.etapa3.ClasesSemantico.EntradaMetodo;

import java.util.Hashtable;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

public class TablaSimbolos {
    private Hashtable<String,EntradaStruct> structs;
    private EntradaStruct currentStruct;
    private EntradaMetodo currentMetod;

    public TablaSimbolos (){
        this.structs = new Hashtable<>();
    }

    // Getters
    public EntradaStruct getCurrentStruct() {
        return currentStruct;
    }

    public EntradaStruct getStruct(String nombre) {
        return this.structs.get(nombre);
    }

    public EntradaMetodo getCurrentMetod() {
        return currentMetod;
    }

    // Setters
    public void setCurrentStruct(EntradaStruct currentStruct) {
        this.currentStruct = currentStruct;
    }

    public void setCurrentMetod(EntradaMetodo currentMetod) {
        this.currentMetod = currentMetod;
    }

    // Functions
    public void insertStruct(EntradaStruct struct, Token token){
        if(this.structs.containsKey(struct.getName())){
            throw new SemantErrorException(token.getLine(), token.getCol(),
                    "Ya existe un struct con el nombre \"" + struct.getName() + "\" ","insertStruct");
        }
        this.structs.put(struct.getName(), struct);
    }


    public boolean searchStruct(String nombre){
        return this.structs.containsKey(nombre);
    }

    public String printJSON_Tabla(){
        String json = "{\n";
        json += "\"structs\": [\n";
        for(Map.Entry<String, EntradaStruct> entry : structs.entrySet()) {
            String key = entry.getKey();
            EntradaStruct value = entry.getValue();
            json +="{\n\t\"nombre\": \""+ value.getName() + "\",\n"+ value.printJSON_Struct()+"\n}\n],";
        }
        json += "\n\"start\": {";
        json +="\n\t\"nombre\": \"start\",\n\t\"retorno\": \"void\",\n\t\"posicion\": 0, \n},";

        json = json.substring(0,json.length()-1);
        json += "\n}";
        return json;
    }

    // Método para guardar el JSON en un archivo
    public void saveJSON(String json, String nombreArchivo) {
        String rutaArchivo = System.getProperty("user.dir") + "\\src\\main\\java\\org\\com\\etapa3\\" + nombreArchivo;
        try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
            fileWriter.write(json);
            System.out.println("JSON guardado exitosamente en " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar el JSON");
            e.printStackTrace();
        }
    }
}