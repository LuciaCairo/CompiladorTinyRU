package org.com.etapa3.ClasesSemantico;

import org.com.etapa3.TablaSimbolos;

import java.util.Map;

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
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {
            String key = entry.getKey();
            EntradaStruct value = entry.getValue();
            System.out.println(value.getName());
            ts.setCurrentStruct(value);
            consolidarAtributosHeredados(value);
        }
    }

    public void consolidarAtributosHeredados(EntradaStruct struct) {
        String herencia = struct.getHerencia();
        if (!"Object".equals(herencia)) {
            EntradaStruct structHeredada = ts.getStruct(herencia);
            if (structHeredada != null) {
                consolidarAtributosHeredados(structHeredada);
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
