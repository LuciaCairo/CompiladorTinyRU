package org.com.etapa3;

import org.com.etapa3.ClasesSemantico.EntradaAtributo;
import org.com.etapa3.ClasesSemantico.EntradaMetodo;
import org.com.etapa3.ClasesSemantico.EntradaParametro;
import org.com.etapa3.ClasesSemantico.EntradaStruct;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Hashtable;
public class AnalizadorSemantico {
    TablaSimbolos ts;

    public AnalizadorSemantico(TablaSimbolos ts){
        this.ts = ts;
    }

    // Funcion para el chequeo de Declaraciones
    public void checkDecl() {

        HashSet<String> enProgreso = new HashSet<>();
        HashSet<String> visitados = new HashSet<>();
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

            // Consolidar los atributos y verificar herencia ciclica
            consolidarAtributosHeredados(value, visitados,enProgreso);

        }
    }


    //Arreglar la fila y la columna, ya que puse cualquier numero
    public void consolidarAtributosHeredados( EntradaStruct struct, HashSet<String> visitados ,HashSet<String> enProgreso ) {

        String nombreStruct = struct.getName();
        if (enProgreso.contains(nombreStruct)) {
            throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia Cíclica", "Analizador Semántico");
        }

        if (!visitados.contains(nombreStruct)) {
            enProgreso.add(nombreStruct);

            String herencia = struct.getHerencia();

            if (!herencia.equals("Object")) {
                EntradaStruct structHeredada = ts.getStruct(herencia);
                //ver si un struct hereda de otro struct que no existe
                if (ts.getStruct(herencia)== null){
                    throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia de struct inexistente. El struct '"+ struct.getName()+
                            "' hereda del struct inexistente: '"+herencia+"' .Primero defina '"+herencia+ "' para poder utilizarlo.", "Analizador Semántico");
                }
                if (structHeredada != null) {
                    consolidarAtributosHeredados(structHeredada, visitados, enProgreso);
                    //inserta los atributos del struct heredado
                    for (EntradaAtributo atributo : structHeredada.getAtributos().values()) {
                            struct.insertAtributoHeredado(atributo.getName(), atributo);

                    }
                    //inserta los metodos del struc heredado
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
