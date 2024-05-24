package org.com.etapa5.TablaDeSimbolos;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.Token;

import java.util.Hashtable;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class TablaSimbolos {
    private Hashtable<String,EntradaStruct> structs;
    private Hashtable<String, EntradaStructPredef> structsPred;
    private EntradaStruct start;
    private EntradaStruct currentStruct;
    private EntradaVariable currentVar;
    private EntradaAtributo currentAtr;
    private EntradaMetodo currentMetod;

    public TablaSimbolos (){
        this.structs = new Hashtable<>();
        this.structsPred = new Hashtable<>();
        this.structsPred.put("Object",new EntradaStructPredef("Object"));
        this.structsPred.put("IO", newIO());
        this.structsPred.put("Array", newArray());
        this.structsPred.put("Int",new EntradaStructPredef("Int"));
        this.structsPred.put("Str", newStr());
        this.structsPred.put("Bool",new EntradaStructPredef("Bool"));
        this.structsPred.put("Char",new EntradaStructPredef("Char"));
    }

    // Funciones para crear clases predefinidas

    // Struct Predefinida Str
    public EntradaStructPredef newStr(){
        EntradaStructPredef e = new EntradaStructPredef("Str");
        // fn length()->Int
        e = insertPred(e, false,"length", "Int", 0);
        // fn concat(Str s)->Str.
        e = insertPred(e, false,"concat", "Str", 1, "s", "Str");

        return e;
    }

    // Struct Predefinida Array
    public EntradaStructPredef newArray(){
        EntradaStructPredef e = new EntradaStructPredef("Array");
        // fn length()->Int
        e = insertPred(e, false,"length", "Int", 0);

        return e;
    }

    // Struct Predefinida IO
    public EntradaStructPredef newIO(){
        EntradaStructPredef e = new EntradaStructPredef("IO");

        // st fn out_str(Str s)->void: imprime el argumento.
        e = insertPred(e, true,"out_str", "void", 0, "s", "Str");
        // st fn out_int(Int i)->void: imprime el argumento
        e = insertPred(e, true,"out_int", "void", 1, "i", "Int");
        // st fn out_bool(Bool b)->void: imprime el argumento.
        e = insertPred(e, true,"out_bool", "void", 2, "b", "Bool");
        // st fn out char(Char c)->void: imprime el argumento.
        e = insertPred(e, true,"out_char", "void", 3, "c", "Char");
        // st fn out array int(Array a)->void: imprime cada elemento del arreglo de tipo Int.
        e = insertPred(e, true,"out_array_int", "void", 4, "a", "Array");
        // st fn out array str(Array a)->void: imprime cada elemento del arreglo de tipo Int.
        e = insertPred(e, true,"out_array_str", "void", 5, "a", "Array");
        // st fn out array bool(Array a)->void: imprime cada elemento del arreglo de tipo Int.
        e = insertPred(e, true,"out_array_bool", "void", 6, "a", "Array");
        // st fn out array char(Array a)->void: imprime cada elemento del arreglo de tipo Int.
        e = insertPred(e, true,"out_array_char", "void", 7, "a", "Array");
        // st fn in_str()->Str: lee una cadena de la entrada est´andar, sin incluir un car´acter de nueval´ınea.
        e = insertPred(e, true,"in_str", "Str", 8);
        // st fn in_int()->Int: lee una cadena de la entrada est´andar, sin incluir un car´acter de nueval´ınea.
        e = insertPred(e, true,"in_int", "Int", 9);
        // st fn in_bool()->Bool: lee una cadena de la entrada est´andar, sin incluir un car´acter de nueval´ınea.
        e = insertPred(e, true,"in_bool", "Bool", 10);
        // st fn in_char()->Char: lee una cadena de la entrada est´andar, sin incluir un car´acter de nueval´ınea.
        e = insertPred(e, true,"in_char", "Char", 11);

        return e;
    }

    public EntradaStructPredef insertPred(EntradaStructPredef e, Boolean st, String name, String ret, int pos, String param, String tipo){
        EntradaMetodo m = new EntradaMetodo(name,st,ret,pos,0,0);
        EntradaParametro p = new EntradaParametro(param, tipo, 0,0,0);
        m.insertParametroPred(param,p);
        e.insertMetodoPred(name,m);
        return e;
    }

    public EntradaStructPredef insertPred(EntradaStructPredef e, Boolean st, String name, String ret, int pos){
        EntradaMetodo m = new EntradaMetodo(name,st,ret,pos,0,0);
        e.insertMetodoPred(name,m);
        return e;
    }

    // Getters
    public EntradaStruct getCurrentStruct() {
        return currentStruct;
    }

    public Hashtable<String,EntradaStruct> getTableStructs() {
        return structs;
    }

    public Hashtable<String,EntradaStructPredef> getStructsPred() {
        return structsPred;
    }

    public EntradaStruct getStruct(String nombre) {
        return this.structs.get(nombre);
    }

    public EntradaStructPredef getStructPred(String nombre) {
        return this.structsPred.get(nombre);
    }

    public EntradaMetodo getCurrentMetod() {
        return currentMetod;
    }

    public EntradaVariable getCurrentVar() {
        return currentVar;
    }
    public EntradaAtributo getCurrentAtr() {
        return currentAtr;
    }

    // Setters
    public void setCurrentStruct(EntradaStruct currentStruct) {
        this.currentStruct = currentStruct;
    }

    public void setCurrentMetod(EntradaMetodo currentMetod) {
        this.currentMetod = currentMetod;
    }

    public void setCurrentVar(EntradaVariable currentVar) {
        this.currentVar = currentVar;
    }

    public void setCurrentAtr(EntradaAtributo currentAtr) {
        this.currentAtr = currentAtr;
    }

    // Functions
    public void insertStruct_struct(EntradaStruct struct, Token token){
        if(struct.gethaveImpl() && !struct.gethaveStruct()){
            this.structs.put(struct.getName(), struct);
        } else {
            if(this.structs.containsKey(struct.getName())){
                throw new SemantErrorException(token.getLine(), token.getCol(),
                        "Ya existe un struct con el nombre \"" + struct.getName() + "\" ","insertStruct");
            }
            this.structs.put(struct.getName(), struct);
        }
    }

    public void insertStruct_impl(EntradaStruct struct, Token token){
        if(struct.gethaveStruct() && !struct.gethaveImpl()){
            this.structs.put(struct.getName(), struct);
        } else {
            if(this.structs.containsKey(struct.getName())){
                throw new SemantErrorException(token.getLine(), token.getCol(),
                        "Ya existe un impl para el struct \"" + struct.getName() + "\" ","insertStruct");
            }
            this.structs.put(struct.getName(), struct);
        }
    }

    public boolean searchStruct(String nombre){
        return this.structs.containsKey(nombre);
    }

    public String printJSON_Tabla(String input){
        this.start = this.structs.remove("start");
        String json = "{\n";
        json += "\"nombre\": \"TS-"+input+"\",\n"; // "TS-ejemplo.ru",
        json += "\"structs\": [\n";
        for(Map.Entry<String, EntradaStructPredef> entry : structsPred.entrySet()) {
            String key = entry.getKey();
            EntradaStructPredef value = entry.getValue();
            json +="{\n\t\"nombre\": \""+ value.getName() + "\",\n"+ value.printJSON_StructPredef()+"\n},\n";
        }
        for(Map.Entry<String, EntradaStruct> entry : structs.entrySet()) {
            String key = entry.getKey();
            EntradaStruct value = entry.getValue();
            json +="{\n\t\"nombre\": \""+ value.getName() + "\",\n"+ value.printJSON_Struct()+"\n},\n";
        }
        json = json.substring(0,json.length()-2);
        json += "\n],";
        json += "\n\"start\": {";
        json +="\n\t\"nombre\": \"start\",\n\t\"retorno\": \"void\",\n\t\"posicion\": 0," + this.start.printJSON_Start() +"\n},";
        json = json.substring(0,json.length()-1);
        json += "\n}";
        return json;
    }

    // Método para guardar el JSON en un archivo
    public void saveJSON(String json, String nombreArchivo) {
        String rutaActual = System.getProperty("user.dir");
        File directorioActual = new File(rutaActual);
        String rutaDirectorioPadre = directorioActual.getParent();
        String rutaArchivo = rutaActual + "\\" + nombreArchivo;
        try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
            fileWriter.write(json);
            System.out.println("JSON guardado exitosamente en " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar el JSON");
            e.printStackTrace();
        }
    }

}