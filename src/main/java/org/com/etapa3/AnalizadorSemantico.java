package org.com.etapa3;

import org.com.etapa3.ArbolAST.AST;
import org.com.etapa3.ArbolAST.NodoMetodo;
import org.com.etapa3.ArbolAST.NodoLiteral;
import org.com.etapa3.ArbolAST.NodoStruct;
import org.com.etapa3.ClasesSemantico.*;


import java.util.*;

public class AnalizadorSemantico {
    TablaSimbolos ts;
    AST ast;
    HashSet<String> enProgreso = new HashSet<>();
    HashSet<String> visitados = new HashSet<>();

    // Constructor
    public AnalizadorSemantico(TablaSimbolos ts, AST ast){
        this.ts = ts;
        this.ast = ast;
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

                        // Verificar que las declaraciones de atributos esten correctas
                        String[] palabras = a.getType().split(" ");
                        String isArray = palabras[0];
                        if(!ts.getTableStructs().containsKey(a.getType())
                                && !ts.getStructsPred().containsKey(a.getType())
                                && !isArray.equals("Array")){
                            throw new SemantErrorException(a.getLine(), a.getCol(), "Tipo no definido: \"" + a.getType() + "\" no esta definido", "AS");
                        };
                    }
                    for (EntradaAtributo atributo : structHeredada.getAtributos().values()) {
                            struct.insertAtributoHeredado(atributo.getName(), atributo,structHeredada.getName());
                    }

                    // Inserta los metodos del struct heredado
                    for (EntradaMetodo m : struct.getMetodos().values()) { // Primero redefino las posiciones
                        m.setPos(m.getPos() + structHeredada.getMetodos().size() - 1); // - 1 porque el constructor no se hereda

                        // Verificar que las declaraciones de retorno de metodos esten correctas
                        if(!m.getName().equals("constructor")){ // Excepto para el constructor (caso de metodo especial sin retorno)
                            String[] palabras = m.getRet().split(" ");
                            String isArray = palabras[0];
                            if(!ts.getTableStructs().containsKey(m.getRet())
                                    && !ts.getStructsPred().containsKey(m.getRet())
                                    && !m.getRet().equals("void")
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(m.getLine(), m.getCol(), "Tipo de retorno no definido: \"" + m.getRet() +
                                        "\" no esta definido", "AS");
                            }
                        }

                        // Ver que declaracion de parametros este bien en metodos (por ejemplo Base b, ver que Base exista)
                        for (EntradaParametro p : struct.getMetodos().get(m.getName()).getParametros().values()) {
                            String[] palabras = p.getType().split(" ");
                            String isArray = palabras[0];
                            if(!ts.getTableStructs().containsKey(p.getType())
                                    && !ts.getStructsPred().containsKey(p.getType())
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(p.getLine(), p.getCol(), "Tipo de parametro no definido: \"" + p.getType() + "\" no esta definido", "AS");
                            };
                        }

                        // Ver que declaracion de variables este bien en metodos (por ejemplo Base b, ver que Base exista)
                        for (EntradaVariable v : struct.getMetodos().get(m.getName()).getVariables().values()) {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if(!ts.getTableStructs().containsKey(v.getType())
                                    && !ts.getStructsPred().containsKey(v.getType())
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(v.getLine(), v.getCol(), "Tipo de variable no definido: \"" + v.getType() + "\" no esta definido", "AS");
                            };
                        }
                    }
                    // Ahora si inserta los metodos heredados
                    for (EntradaMetodo metodo : structHeredada.getMetodos().values()) {
                        String nameMetodoHeredado = metodo.getName();

                        // El constructor no se hereda
                        if(!(nameMetodoHeredado.equals("constructor"))){

                            // Verifica si hay metodos heredados redefinidos
                            if(struct.getMetodos().containsKey(nameMetodoHeredado)){

                                // Si hay un metodo redefinido verifica que la firma sea igual a la del metodo heredado
                                igualFirma(struct.getMetodos().get(nameMetodoHeredado),metodo);
                                // Si el metodo tiene la misma firma, entonces no se agrega el metodo ancestro.
                                // Solo se deja el redefinido ya que tiene prioridad. Pero le cambio la posicion
                                struct.getMetodos().get(nameMetodoHeredado).setPos(metodo.getPos());

                            } else{
                                // Inserta los metodos del struct heredado que no estan redefinidos
                                struct.getMetodos().put(nameMetodoHeredado, metodo);
                            }
                        }

                    }

                }
            } else{
                // Verificar que todo nombre usado en una declaración haya sido declarado en el contexto adecuado.

                // Primero verifico si el struct es start (ya que es un caso especial de struct)
                if(struct.getName().equals("start")){
                    // Para start solo hay que verificar si la declaracion de variables es correcta
                    // (por ejemplo: Base b, ver que Base exista)
                    for (EntradaVariable v : struct.getVariables().values()) {
                        String[] palabras = v.getType().split(" ");
                        String isArray = palabras[0];
                        if(!ts.getTableStructs().containsKey(v.getType()) && !ts.getStructsPred().containsKey(v.getType())
                                && !isArray.equals("Array")){
                            throw new SemantErrorException(v.getLine(), v.getCol(), "Tipo no definido: el tipo \"" + v.getType() +
                                    "\" de la variable \"" + v.getName() + "\" no esta definido", "AS");
                        };
                    }
                } else { // Ahora para los demas structs si verifico todo

                    // Verificar que las declaraciones de atributos esten correctas
                    for (EntradaAtributo a : struct.getAtributos().values()) {
                        String[] palabras = a.getType().split(" ");
                        String isArray = palabras[0];
                        if(!ts.getTableStructs().containsKey(a.getType()) && !ts.getStructsPred().containsKey(a.getType())
                                && !isArray.equals("Array")){
                            throw new SemantErrorException(a.getLine(), a.getCol(), "Tipo no definido: el tipo \"" + a.getType() +
                                    "\" del atributo \"" + a.getName() +"\" no esta definido", "AS");
                        };
                    }

                    // Verificar que las declaraciones en metodos esten correctas
                    for (EntradaMetodo m : struct.getMetodos().values()) {

                        // Verifico si el metodo es el constructor (ya que es un caso especial de metodo)
                        // Para constructor no hay que verificar si la declaracion de retorno es correcta
                        if(!m.getName().equals("constructor")){
                            String[] palabras = m.getRet().split(" ");
                            String isArray = palabras[0];
                            if((!ts.getTableStructs().containsKey(m.getRet()) // Verificar las declaraciones de retorno
                                    && !ts.getStructsPred().containsKey(m.getRet()))
                                    && !m.getRet().equals("void")
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(m.getLine(), m.getCol(), "Tipo no definido: el tipo \"" + m.getRet() +
                                        "\" de retorno no esta definido", "AS");
                            };
                        }

                        // Ver que declaracion de parametros en metodos esten correctas (por ejemplo Base b, ver que Base exista)
                        for (EntradaParametro p : struct.getMetodos().get(m.getName()).getParametros().values()) {
                            String[] palabras = p.getType().split(" ");
                            String isArray = palabras[0];
                            if(!ts.getTableStructs().containsKey(p.getType()) && !ts.getStructsPred().containsKey(p.getType())
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(p.getLine(), p.getCol(), "Tipo de parametro no definido: \"" + p.getType() + "\" no esta definido", "AS");
                            };
                        }

                        // Ver que declaracion de variables este bien en metodos (por ejemplo Base b, ver que Base exista)
                        for (EntradaVariable v : struct.getMetodos().get(m.getName()).getVariables().values()) {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if(!ts.getTableStructs().containsKey(v.getType()) && !ts.getStructsPred().containsKey(v.getType())
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(v.getLine(), v.getCol(), "Tipo de variable no definido: \"" + v.getType() + "\" no esta definido", "AS");
                            };
                        }
                    }
                }
            }
            visitados.add(nombreStruct);
            enProgreso.remove(nombreStruct);
        }

    }

    public static void igualFirma(EntradaMetodo metodo1, EntradaMetodo metodo2) {
        //Verificar que los metodos sean st
        if (metodo1.getSt()) {
            // falta st en el metodo
            throw new SemantErrorException(metodo1.getLine(), metodo1.getCol(),
                    "No se puede redefinir el metodo heredado \"" + metodo1.getName() +
                            "\" porque es estatico"
                    ,"insertMetodoHeredado");
        }

        // Verificar que tengan el mismo tipo de retorno
        if (!metodo1.getRet().equals(metodo2.getRet())) {
            throw new SemantErrorException(metodo1.getLine(), metodo1.getCol(),
                    "No se puede redefinir el metodo heredado \"" + metodo1.getName() +
                            "\" porque no retorna el mismo tipo que su metodo heredado. " +
                            "Para redefinir un metodo este debe deben tener la misma firma que su metodo ancestro"
                    ,"insertMetodoHeredado");
        }

        // Verificar cantidad de parámetros
        if (metodo1.getParametros().size() != metodo2.getParametros().size()) {
            throw new SemantErrorException(metodo1.getLine(), metodo1.getCol(),
                    "No se puede redefinir el metodo heredado \"" + metodo1.getName() +
                            "\" porque no tiene la misma cantidad de parametros que su metodo heredado. " +
                            "Para redefinir un metodo este debe deben tener la misma firma que su metodo ancestro"
                    ,"insertMetodoHeredado");
        }

        // Lista de parametros del metodo1 ordenados por posicion
        Hashtable<String, EntradaParametro> parametrosM1 = metodo1.getParametros();
        List<EntradaParametro> parametrosOrdenadosM1 = new ArrayList<>(parametrosM1.values());
        parametrosOrdenadosM1.sort(Comparator.comparingInt(EntradaParametro::getPos));

        // Lista de parametros del metodo2 ordenados por posicion
        Hashtable<String, EntradaParametro> parametrosM2 = metodo2.getParametros();
        List<EntradaParametro> parametrosOrdenadosM2 = new ArrayList<>(parametrosM2.values());
        parametrosOrdenadosM2.sort(Comparator.comparingInt(EntradaParametro::getPos));

        // Verificar que tengan los mismos tipos y nombres de parametros (y en el mismo orden)
        for (EntradaParametro currentParam1 : parametrosOrdenadosM1) {

            EntradaParametro currentParam2 = parametrosOrdenadosM2.get(currentParam1.getPos());

            // Verifica que los parametros tengan el mismo nombre
            if(!(currentParam1.getName().equals(currentParam2.getName()))){
                throw new SemantErrorException(metodo1.getLine(), metodo1.getCol(),
                        "No se puede redefinir el metodo heredado \"" + metodo1.getName() +
                                "\" porque el parametro formal en la posicion "+ currentParam1.getPos() +" tiene distinto nombre. " +
                                "Para redefinir un metodo este debe deben tener la misma firma que su metodo ancestro"
                        ,"insertMetodoHeredado");
            }

            // Verifica que los parametros tengan el mismo tipo
            if(!(currentParam1.getType().equals(currentParam2.getType()))){
                throw new SemantErrorException(metodo1.getLine(), metodo1.getCol(),
                        "No se puede redefinir el metodo heredado \"" + metodo1.getName() +
                                "\" porque el parametro formal en la posicion "+ currentParam1.getPos() +" tiene distinto tipo. " +
                                "Para redefinir un metodo este debe deben tener la misma firma que su metodo ancestro"
                        ,"insertMetodoHeredado");
            }
        }

        // Si pasó todas las verificaciones, no hay error
    }

    // Funcion para el chequeo de Sentencias
    public void checkSent() {
        // En esta funcion se recorren los nodos del AST y
        // para cada uno de ellos se realiza un chequeo de sus tipos
        // Recorro cada struct
        for (Map.Entry<String, NodoStruct> entry : ast.getStructs().entrySet()) {
            NodoStruct value = entry.getValue();
            ast.setCurrentStruct(value);

            // Primero verifico si el struct es start (ya que es un caso especial de struct)
            if(value.getName().equals("START")){
                // El start no tiene metodos
                // start{ sentencias }
                /*for (EntradaVariable v : struct.getVariables().values()) {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if(!ts.getTableStructs().containsKey(v.getType()) && !ts.getStructsPred().containsKey(v.getType())
                                    && !isArray.equals("Array")){
                                throw new SemantErrorException(v.getLine(), v.getCol(), "Tipo no definido: el tipo \"" + v.getType() +
                                        "\" de la variable \"" + v.getName() + "\" no esta definido", "AS");
                            };
                        }*/
            } else { // Ahora para los demas structs que no son start

                // Recorro todos los nodos metodos del struct
                for (NodoMetodo m : value.getMetodos().values()) {
                    ts.setCurrentStruct(ts.getStruct(value.getName()));
                    ts.setCurrentMetod(ts.getCurrentStruct().getMetodo(m.getName()));

                    boolean isRet = false;
                    // Recorro las sentencias del metodo
                    // Las sentencias pueden ser:
                    // (1)sentencia simple, (2)asignacion, (3)bloque, (4)if, (5)while o (6)retorno
                    for (NodoLiteral s : m.getSentencias()) {
                        if(s.getName()!= null){
                            if(s.getName()!= null && s.getName().equals("Retorno")){
                                isRet = true;
                            }
                        }

                        // Para cada sentencia asigno y verifico sus tipos
                        s.checkTypes(ts);

                    }
                    if(!isRet){
                        if( !(ts.getStruct(value.getName()).getMetodos().get(m.getName()).getRet().equals("void"))){
                            throw new SemantErrorException(m.getLine(), m.getCol(),
                                    "Falta sentencia de retorno. El metodo esta delarado para retornar " +
                                            ts.getStruct(value.getName()).getMetodos().get(m.getName()).getRet(),
                                    "sentencia");
                        }
                    }
                }
            }
        }
    }
}
