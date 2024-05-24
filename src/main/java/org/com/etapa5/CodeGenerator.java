package org.com.etapa5;

import org.com.etapa5.ArbolAST.AST;
import org.com.etapa5.ArbolAST.NodoLiteral;
import org.com.etapa5.ArbolAST.NodoMetodo;
import org.com.etapa5.ArbolAST.NodoStruct;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.*;

import java.util.*;

public class CodeGenerator {
    TablaSimbolos ts;
    AST ast;

    // Constructor
    public CodeGenerator(TablaSimbolos ts, AST ast){
        this.ts = ts;
        this.ast = ast;
    }

    // Getters

    // Setters

    // Functions

    // Funcion para genera el codigo recorriendo la TS y luego el AST
    public void generateCode() {
        System.out.println(".data");

        // Generar declaraciones de variables a partir de la TS
        generateData();

        System.out.println(".text");
        System.out.println("main:");

        // Generar texto a partir del AST
        generateText();  // ACA HAY QUE HACER UNA FUNCION QUE RECORRA EL AST Y VAYA GENERANDO EL
        // CODIGO SEGUN CADA NODO, COMO EL CHECKTYPES
        System.out.println("li $v0, 10"); // Finalizar programa
        System.out.println("syscall");
    }

    // Funcion para generar la data recorriendo la TS
    public void generateData() {

        // Recorro los structs de la TS
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {

            EntradaStruct struct = entry.getValue();
            ts.setCurrentStruct(struct);

            String nombreStruct = struct.getName();

            // Primero verifico si el struct es start (ya que es un caso especial de struct)
            if(struct.getName().equals("start")){

                // Start solo tiene variables
                for (EntradaVariable v : struct.getVariables().values()) {

                    // ACA NOSE SI ESTA BIEN ESTO DE MIPS
                    System.out.println(v.getName() + ": .word 0");
                }

            } else { // Ahora para los demas structs

                // Recorro los atributos del struct
                for (EntradaAtributo a : struct.getAtributos().values()) {

                    // ACA NOSE SI ESTA BIEN ESTO DE MIPS
                    System.out.println(a.getName() + ": .word 0");
                }

                // Recorro los metodos del struct
                for (EntradaMetodo m : struct.getMetodos().values()) {


                    // Caso especial del constructor
                    //if(m.getName().equals("constructor")){
                        // NECESITA CASO APARTE? CREO QUE NO
                    //}

                    // Recorro los parametros del metodo
                    for (EntradaParametro p : struct.getMetodos().get(m.getName()).getParametros().values()) {
                        // ACA NOSE SI ESTA BIEN ESTO DE MIPS
                        System.out.println(p.getName() + ": .word 0");
                    }

                    // Recorro las variables del metodo
                    for (EntradaVariable v : struct.getMetodos().get(m.getName()).getVariables().values()) {
                        // ACA NOSE SI ESTA BIEN ESTO DE MIPS
                        System.out.println(v.getName() + ": .word 0");
                    }
                }
            }

        }
    }



    // Funcion para generar el text recorriendo el AST
    public void generateText() {
        // En esta funcion se recorren los nodos del AST y
        // para cada uno de ellos se realiza la generacion de codigo en MIPS

        // Recorro cada struct
        for (Map.Entry<String, NodoStruct> entry : ast.getStructs().entrySet()) {
            NodoStruct value = entry.getValue();
            ast.setCurrentStruct(value);

            // Primero verifico si el struct es start (ya que es un caso especial de struct)
            if(value.getName().equals("start")){
                // El start no tiene metodos start{ sentencias }
                ts.setCurrentStruct(ts.getStruct(value.getName()));

                // Recorro las sentencias del start
                if(!value.getSentencias().isEmpty()) {
                    for (NodoLiteral s : value.getSentencias()) {

                        // Para cada sentencia genero codigo
                        s.generateNodeCode();

                    }
                }

            } else { // Ahora para los demas structs que no son start

                // Recorro todos los nodos metodos del struct
                for (NodoMetodo m : value.getMetodos().values()) {
                    ts.setCurrentStruct(ts.getStruct(value.getName()));
                    ts.setCurrentMetod(ts.getCurrentStruct().getMetodo(m.getName()));

                    // Recorro las sentencias del metodo
                    if(!m.getSentencias().isEmpty()){

                        for (NodoLiteral s : m.getSentencias()) {

                            // Para cada sentencia genero codigo
                            s.generateNodeCode();

                        }
                    }
                }
            }
        }
    }
}
