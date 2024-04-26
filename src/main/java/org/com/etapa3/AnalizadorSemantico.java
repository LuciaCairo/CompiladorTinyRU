package org.com.etapa3;

import org.com.etapa3.ClasesSemantico.EntradaAtributo;
import org.com.etapa3.ClasesSemantico.EntradaMetodo;
import org.com.etapa3.ClasesSemantico.EntradaParametro;
import org.com.etapa3.ClasesSemantico.EntradaStruct;


import java.util.HashSet;
import java.util.Map;
public class AnalizadorSemantico {
    TablaSimbolos ts;
    HashSet<String> enProgreso = new HashSet<>();
    HashSet<String> visitados = new HashSet<>();

    // Constructor
    public AnalizadorSemantico(TablaSimbolos ts){
        this.ts = ts;
    }

    // Funcion para el chequeo de Declaraciones
    public void checkDecl() {

        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {

            String key = entry.getKey();
            EntradaStruct value = entry.getValue();
            ts.setCurrentStruct(value);

            // Verifico que todas las clases esten declaradas con struct
            if(!value.gethaveStruct()){
                throw new SemantErrorException(value.getLine(), value.getCol(),
                        "Definicion de estructura incompleta. No se declaro un struct para la clase \"" + value.getName() + "\" ","consolidacion");
            }

            // Verifico que todas las clases tengan impl
            if(!value.gethaveImpl()){
                throw new SemantErrorException(value.getLine(), value.getCol(),
                        "Definicion de estructura incompleta. No se declaro un impl para la clase \"" + value.getName() + "\" ","consolidacion");
            }

            // Verifico que todas las clases tengan un constructor
            if(!value.gethaveConst()){
                throw new SemantErrorException(value.getLine(), value.getCol(),
                        "No se definio un constructor para la clase \"" + value.getName() + "\"","printJasonTabla");
            }

            // Consolidar la Tabla de Simbolos
            consolidacion(value,value);

        }
    }

    // Funcion para consolidar atributos
    public void consolidacion(EntradaStruct struct, EntradaStruct last) {

        String nombreStruct = struct.getName();
        String herencia = struct.getHerencia();

        // Verifico la herencia circular
        if (enProgreso.contains(nombreStruct)) {
            throw new SemantErrorException(last.getLine(), last.getCol(), "Herencia Cíclica en la clase " + last.getName(), "AS");
        }

        if (!visitados.contains(nombreStruct)) {
            enProgreso.add(nombreStruct);

            if (!herencia.equals("Object")) {

                EntradaStruct structHeredada = ts.getStruct(herencia);

                // Verifico si un struct hereda de otro struct que no existe
                if (structHeredada == null){
                    throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia de struct inexistente. El struct \""+ struct.getName()+
                            "\" hereda del struct inexistente: \""+herencia+"\" .Primero defina \""+herencia+ "\" para poder utilizarlo.", "Analizador Semántico");

                } else {

                    consolidacion(structHeredada, struct); // Se recorre hasta que se llega a la clase que hereda de "Object"

                    // Inserta los atributos del struct heredado
                    for (EntradaAtributo a : struct.getAtributos().values()) { // Primero redefino las posiciones
                        a.setPos(a.getPos()+ structHeredada.getAtributos().size());
                    }
                    for (EntradaAtributo atributo : structHeredada.getAtributos().values()) {
                            struct.insertAtributoHeredado(atributo.getName(), atributo,structHeredada.getName());
                    }

                    // Inserta los metodos del struct heredado
                    for (EntradaMetodo metodo : structHeredada.getMetodos().values()) {
                        if (struct.isMetodo(metodo) == true){
                            for(EntradaParametro parametro: metodo.getParametros().values()){
                                //if(parametro.getName() == struct.){

                                //}
                            }
                        }
                        struct.insertMetodoHeredado(metodo.getName(), metodo);

                    }

                }
            }

            visitados.add(nombreStruct);
            enProgreso.remove(nombreStruct);
        }



    }




}
