package org.com.etapa3;

import org.com.etapa3.ClasesSemantico.EntradaAtributo;
import org.com.etapa3.ClasesSemantico.EntradaMetodo;
import org.com.etapa3.ClasesSemantico.EntradaParametro;
import org.com.etapa3.ClasesSemantico.EntradaStruct;


import java.util.HashSet;
import java.util.Hashtable;
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
                    //ejemplo A:B (metodo igual es pepe)
                    //Creo metodosStrcut para guardar los metodos que tiene el struct que estoy analizando (A)
                    Hashtable<String, EntradaMetodo> metodosStrcut= struct.getMetodos();
                    // Para cada uno de los metodos del struct del que heredo (B)
                    for (EntradaMetodo metodo : structHeredada.getMetodos().values()) {
                        //Pregunto si el metodo heredado existe en el struct actual (A)
                        if (struct.isMetodo(metodo)){ //si el metodo esta en el struc q estoy revisando (si el metodo esta en A)
                            //Creo una entradaMetodo para almacenar el metodo del struct actual (pepe)
                            EntradaMetodo metodoSTRCUT= metodosStrcut.get(metodo.getName());
                            //Creo una hash de parametros, para guardar todos los parametros que tiene el metodo PEPE en la clase A
                            Hashtable<String, EntradaParametro> parametrosMetodo =  metodoSTRCUT.getParametros();
                            System.out.println(metodoSTRCUT);
                            // Recorro todos los parametros que contiene el metodo Pepe de la clase B
                            for(EntradaParametro parametro: metodo.getParametros().values()){ //para cada parametro del metodo heredado
                                //Si los parametros de Pepe de la clase A contienen al parametro que estoy analizando del metodo Pepe de B
                                if (parametrosMetodo.get(parametro)!= null){

                                    EntradaParametro parametro1 = parametrosMetodo.get(parametro);
                                    //Analizo los tipos
                                    if (parametro1.getTipo() == parametro.getTipo()){
                                        continue;
                                    } else{
                                        throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia1", "Analizador Semántico");
                                    }


                                }else{// quiere decir que el metodo de la Clase A, no contiene los mismos parametros que B
                                    throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia2", "Analizador Semántico");
                                }


                                //if (.getParametros().get(parametro).getTipo() == parametro.getTipo()  ){

                                //} else{
                                  //  throw new SemantErrorException(struct.getLine(), struct.getCol(), "Herencia", "Analizador Semántico");
                                //}
                            }
                            struct.insertMetodoHeredado(metodo.getName(), metodo); // Si termina es porq esta todo ok, netonces inserto
                        }else{
                            struct.insertMetodoHeredado(metodo.getName(), metodo); //si el metodo no tiene la misma firma, inserto
                        }


                    }

                }
            }

            visitados.add(nombreStruct);
            enProgreso.remove(nombreStruct);
        }



    }




}
