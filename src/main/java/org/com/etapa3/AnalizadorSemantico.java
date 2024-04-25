package org.com.etapa3;

import org.com.etapa3.ClasesSemantico.EntradaAtributo;
import org.com.etapa3.ClasesSemantico.EntradaStruct;


import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
public class AnalizadorSemantico {
    TablaSimbolos ts;

    public AnalizadorSemantico(TablaSimbolos ts){
        this.ts = ts;
    }

    // Funcion para el chequeo de Declaraciones
    public void checkDecl() {
        Map<String, Boolean> structsVisitados = new HashMap<>();
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {
            String key = entry.getKey();
            EntradaStruct value = entry.getValue();

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

            // AGUS ACA ESTA LO QUE ESTABAS HACIENDO VOS -------------------
            System.out.println(value.getName());
            ts.setCurrentStruct(value);
            consolidarAtributosHeredados(value, structsVisitados);
        }
    }


    public void consolidarAtributosHeredados( EntradaStruct struct, Map<String, Boolean> structsVisitados) {
        if (structsVisitados.containsKey(struct.getName())) {
            throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia Ciclica","Analizador Semantico");
        }
        structsVisitados.put(struct.getName(), true);
        String herencia = struct.getHerencia();
        if (!herencia.equals("Object")) {
            EntradaStruct structHeredada = ts.getStruct(herencia);
            if (structHeredada != null) {
                consolidarAtributosHeredados(structHeredada,structsVisitados);
                for (Map.Entry<String, EntradaAtributo> entryAtrib : structHeredada.getAtributos().entrySet()) {
                    String keyAtr = entryAtrib.getKey();
                    EntradaAtributo valueAtr = entryAtrib.getValue();
                    struct.insertAtributoHeredado(keyAtr, valueAtr);
                }
            }
        }
    }
    public void consolidacion8() {
        for(Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {
            String key = entry.getKey();
            EntradaStruct value = entry.getValue();
            System.out.println(value.getName());
            ts.setCurrentStruct(value);
            String h = value.getHerencia();
            System.out.println(h);
            if(h != "Object"){
                EntradaStruct b = ts.getStruct(h);
                for(Map.Entry<String, EntradaAtributo> entryAtrib : b.getAtributos().entrySet()) {
                    String keyAtr = entry.getKey();
                    EntradaAtributo valueAtr = entryAtrib.getValue();
                    ts.getCurrentStruct().insertAtributoHeredado(keyAtr,valueAtr);
                }
            } else{

            }
        }
    }

}
