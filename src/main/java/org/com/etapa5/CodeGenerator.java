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
    //private Hashtable<String,String> data = new Hashtable<>();
    private List<AbstractMap.SimpleEntry<String, String>> data = new ArrayList<>();
    private String text = "";
    String code = "";
    public static int registerCounter = 0;
    public static String lit = "";

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

        // Generar declaraciones de variables a partir de la TS
        generateData();

        // Generar codigo a partir del AST
        generateText();

        // Generar codigo a partir de los metodos predefinidos
        generatePred();

        // Ahora genero el codigo MIPS ordenado
        code += ".data\n";
        for(AbstractMap.SimpleEntry<String, String> d : data) {
            code += d.getValue() + "\n";
        }

        code += ".text\n";
        code += ".globl main\n";
        code += this.text;
        System.out.println(code);
    }

    // Funcion para generar la data recorriendo la TS
    public void generateData() {

        // Recorro los structs predefinidos de la TS
        for (Map.Entry<String, EntradaStructPredef> entry : ts.getStructsPred().entrySet()) {

            EntradaStructPredef struct = entry.getValue();
            //this.data.put(struct.getName(), struct.getName()+"_vtable:");
            this.data.add(new AbstractMap.SimpleEntry<>(struct.getName(), struct.getName()+"_vtable:"));

            // Los structs predefinidos no tienen atributos
            // Entonces solo recorro los metodos del struct
            for (EntradaMetodo m : struct.getMetodos().values()) {
                //this.data.put(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName());
                this.data.add(new AbstractMap.SimpleEntry<>(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName()));

                // Recorro los parametros del metodo
                for (EntradaParametro p : struct.getMetodos().get(m.getName()).getParametros().values()) {

                    if(p.getType().equals("Int")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .word 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .word 0\n"));

                    }else if(p.getType().equals("Char") || p.getType().equals("Str")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .asciiz " + " \n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .asciiz " + " \n"));
                    } else if(p.getType().equals("Bool")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .word 1\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .word 1\n"));
                    } else if(p.getType().equals("Bool")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .space 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .space 0\n"));
                    } else {
                        String[] palabras = p.getType().split(" ");
                        String isArray = palabras[0];
                        if (isArray.equals("Array")) {
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .space 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .space 0\n"));
                        } else {
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .space 8\n"));
                        }
                    }

                }
            }
        }

        // Recorro los structs de la TS
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {

            EntradaStruct struct = entry.getValue();
            //this.data.put(struct.getName(), struct.getName()+"_vtable:");

            // Primero verifico si el struct es start (ya que es un caso especial de struct)
            if(struct.getName().equals("start")){

                // Start solo tiene variables
                for (EntradaVariable v : struct.getVariables().values()) {
                    if(v.getType().equals("Int")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .word 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                "\n" + struct.getName() + "_" + v.getName() + ": .word 0\n"));
                    } else if(v.getType().equals("Char") || v.getType().equals("Str")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .asciiz " + " \n");
                        this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                "\n" + struct.getName() + "_" + v.getName() + ": .asciiz " + " \n"));
                    } else if(v.getType().equals("Bool")){
                            //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                            //        + v.getName() + ": .word 1\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                    "\n" + struct.getName() + "_" + v.getName() + ": .word 1\n"));
                        } else {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if (isArray.equals("Array")) {
                                //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                                //        + v.getName() + ": .space 0\n");
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                        "\n" + struct.getName() + "_" + v.getName() + ": .space 0\n"));
                            } else {
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                        "\n" + struct.getName() + "_" + v.getName() + ": .space 8\n"));
                            }
                        }
                    }

            } else { // Ahora para los demas structs
                this.data.add(new AbstractMap.SimpleEntry<>(struct.getName(), struct.getName()+"_vtable:"));

                // Recorro los metodos del struct
                for (EntradaMetodo m : struct.getMetodos().values()) {

                    this.data.add(new AbstractMap.SimpleEntry<>(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName()));

                    // Recorro los parametros del metodo
                    /*for (EntradaParametro p : struct.getMetodos().get(m.getName()).getParametros().values()) {
                        if(p.getType().equals("Int")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .word 0\n"));

                        }else if(p.getType().equals("Char") || p.getType().equals("Str")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .asciiz " + " \n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .asciiz " + " \n"));
                        }else if(p.getType().equals("Bool")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 1\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .word 1\n"));
                        } else {
                            String[] palabras = p.getType().split(" ");
                            String isArray = palabras[0];
                            if (isArray.equals("Array")) {
                                //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                                //        + p.getName() + ": .space 0\n");
                                this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                        "_" + m.getName() + "_" + p.getName() + ": .space 0\n"));
                            } else {
                                this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                        "_" + m.getName() + "_" + p.getName() + ": .space 8\n"));
                            }
                        }
                    }*/

                    // Recorro las variables del metodo
                    for (EntradaVariable v : struct.getMetodos().get(m.getName()).getVariables().values()) {
                        if(v.getType().equals("Int")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .word 0\n"));

                        } else if(v.getType().equals("Char") || v.getType().equals("Str")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .asciiz " + " \n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .asciiz " + " \n"));
                        } else if(v.getType().equals("Bool")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 1\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .word 1\n"));
                        } else {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if (isArray.equals("Array")) {
                                //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                                //        + p.getName() + ": .space 0\n");
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                        "_" + m.getName() + "_" + v.getName() + ": .space 0\n"));
                            } else {
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                        "_" + m.getName() + "_" + v.getName() + ": .space 8\n"));
                            }
                        }
                    }
                }

                // Recorro los atributos del struct
                for (EntradaAtributo a : struct.getAtributos().values()) {
                    if(a.getType().equals("Int")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .word 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\n" + struct.getName() + "_" + a.getName() + ": .word 0\n"));
                    } else if(a.getType().equals("Char") || a.getType().equals("Str")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .asciiz " + " \n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\n" + struct.getName() + "_" + a.getName() + ": .asciiz " + " \n"));
                    } else if(a.getType().equals("Bool")){
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .word 1\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\n" + struct.getName() + "_" + a.getName() + ": .word 1\n"));
                    } else {
                        String[] palabras = a.getType().split(" ");
                        String isArray = palabras[0];
                        if (isArray.equals("Array")) {
                            //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                            //        + v.getName() + ": .space 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                    "\n" + struct.getName() + "_" + a.getName() + ": .space 0\n"));
                        } else {
                            this.data.add(new AbstractMap.SimpleEntry<>(a.getName(), "\n" + struct.getName() +
                                    "_" + struct.getName() + "_" + a.getName() + ": .space 8\n"));
                        }
                    }
                    // FALTA CASO DE QUE VENGA ALGO DE TIPO CLASE , guardar como null ?
                }
            }

        }
    }


    // Funcion para generar el text recorriendo el AST
    public void generateText() {
        // En esta funcion se recorren los nodos del AST y
        // para cada uno de ellos se realiza la generacion de codigo en MIPS

        // Comienzo con start
        NodoStruct value = ast.getStructs().get("start");
        ast.setCurrentStruct(value);
        ts.setCurrentStruct(ts.getStruct(value.getName()));

        this.text += "main:\n";
        //int numVar = (ts.getCurrentStruct().getVariables().size() + 1) * 4 * (-1);
        //this.text += "addi $sp, $sp," + numVar +
        //        "\nsw $fp, " + ((numVar + 4) * -1) + "($sp)" +
        //        "\nmove $fp, $sp\n";


        // Recorro las sentencias de start
        if (!value.getSentencias().isEmpty()) {
            for (NodoLiteral s : value.getSentencias()) {
                // Para cada sentencia genero codigo
                this.text += s.generateNodeCode(ts);
            }
            this.text +="li $v0, 10\nsyscall\n";
        }

        /*this.text += "move $sp, $fp" +
                "\nlw $fp, " + ((numVar + 4) * -1) + "($sp)" +
                "\naddi $sp, $sp," + numVar + "\n";*/


        // Recorro cada struct
        for (Map.Entry<String, NodoStruct> entry : ast.getStructs().entrySet()) {
            value = entry.getValue();
            ast.setCurrentStruct(value);

            if(!value.getName().equals("start")){

                // Recorro todos los nodos metodos del struct
                for (NodoMetodo m : value.getMetodos().values()) {
                    ts.setCurrentStruct(ts.getStruct(value.getName()));
                    ts.setCurrentMetod(ts.getCurrentStruct().getMetodo(m.getName()));

                    if(m.getName().equals("constructor")) {
                        this.text += "\n" + value.getName() + "_" + m.getName() + " :\n"
                                + "addiu $sp, $sp, -8\n"
                                + "move $v0, $sp\n"
                                + "la $t0, " + value.getName() + "_vtable\n"
                                + "sw $t0 ,0($v0)\n"
                                + "jr $ra\n";
                    }

                    // Recorro las sentencias del metodo
                    if(!m.getSentencias().isEmpty()){
                        this.text += "\n" +value.getName() + "_" + m.getName() + " :\n";
                        for (NodoLiteral s : m.getSentencias()) {

                            // Para cada nodo genero codigo
                            this.text += s.generateNodeCode(ts);

                        }
                    }
                }
            }
        }
    }

    private void generatePred(){

        // IO
        // Código MIPS para la función out_int
        // st fn out_str(Str s)->void: imprime el argumento

        text += "IO_out_int: \n";
        text += "li $v0, 1 \n syscall\n";

    }


    public static int getNextRegister() {
        if(registerCounter == 9){
            resetRegisterCounter();
            return registerCounter;
        } else {
            return registerCounter++;
        }
    }

    public static int getBefRegister() {
        return registerCounter - 1;
    }

    public static void resetRegisterCounter() {
        registerCounter = 0;
    }

    public static String generateLabel(TablaSimbolos ts,String value) {
        // Implementar la generación de etiquetas únicas
        if(ts.getCurrentStruct().getName().equals("start")){
            return ts.getCurrentStruct().getName() +"_"+ value;
        }
        return ts.getCurrentStruct().getName() +"_"+ ts.getCurrentMetod().getName()+"_"+ value;
    }

}
