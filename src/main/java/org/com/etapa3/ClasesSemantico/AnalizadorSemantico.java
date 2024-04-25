package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
public class AnalizadorSemantico {
    TablaSimbolos ts;

    public AnalizadorSemantico(TablaSimbolos ts){
        this.ts = ts;
    }

    public void checkDecl() {
        consolidacion();
    }

    public void checkEnt() {

    }
    public void consolidacion() {
        Map<String, Boolean> structsVisitados = new HashMap<>();
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {
            String key = entry.getKey();
            EntradaStruct value = entry.getValue();
            System.out.println(value.getName());
            ts.setCurrentStruct(value);
            consolidarAtributosHeredados(value, structsVisitados);
        }
    }
    //Arreglar la fila y la columna, ya que puse cualquier numero
    public void consolidarAtributosHeredados( EntradaStruct struct, Map<String, Boolean> structsVisitados) {
        if (structsVisitados.containsKey(struct.getName())) {
            throw new SemantErrorException(1, 2, "Herencia Ciclica","Analizador Semantico");

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
