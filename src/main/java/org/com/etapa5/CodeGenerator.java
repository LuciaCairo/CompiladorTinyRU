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

        // Generar codigo a partir de los metodos predefinidos
        generatePred();

        // Generar codigo a partir del AST
        generateText();

        // Ahora genero el codigo MIPS ordenado
        code += ".data\n";
        for(AbstractMap.SimpleEntry<String, String> d : data) {
            code += d.getValue() + "\n";
        }

        code += "\n.text\n";
        code += this.text;

        code +="li $v0, 10\n syscall";
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

                    }
                    if(p.getType().equals("Char") || p.getType().equals("Str")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .asciiz " + " \n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .asciiz " + " \n"));
                    }
                    if(p.getType().equals("Bool")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .word 1\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .word 1\n"));
                    }
                    String[] palabras = p.getType().split(" ");
                    String isArray = palabras[0];
                    if(isArray.equals("Array")){
                        //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                        //        + p.getName() + ": .space 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                "_" + m.getName() + "_" + p.getName() + ": .space 0\n"));
                    }
                    // FALTA CASO DE QUE VENGA ALGO DE TIPO CLASE , guardar como null ?
                }
            }
        }

        // Recorro los structs de la TS
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {

            EntradaStruct struct = entry.getValue();
            //this.data.put(struct.getName(), struct.getName()+"_vtable:");
            this.data.add(new AbstractMap.SimpleEntry<>(struct.getName(), struct.getName()+"_vtable:"));

            // Primero verifico si el struct es start (ya que es un caso especial de struct)
            if(struct.getName().equals("start")){

                // Start solo tiene variables
                for (EntradaVariable v : struct.getVariables().values()) {
                    if(v.getType().equals("Int")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .word 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                "\t\t" + struct.getName() + "_" + v.getName() + ": .word 0\n"));
                    }
                    if(v.getType().equals("Char") || v.getType().equals("Str")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .asciiz " + " \n");
                        this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                "\t\t" + struct.getName() + "_" + v.getName() + ": .asciiz " + " \n"));
                    }
                        if(v.getType().equals("Bool")){
                            //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                            //        + v.getName() + ": .word 1\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                    "\t\t" + struct.getName() + "_" + v.getName() + ": .word 1\n"));
                        }
                        String[] palabras = v.getType().split(" ");
                        String isArray = palabras[0];
                        if(isArray.equals("Array")){
                            //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                            //        + v.getName() + ": .space 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                    "\t\t" + struct.getName() + "_" + v.getName() + ": .space 0\n"));
                        }
                        // FALTA CASO DE QUE VENGA ALGO DE TIPO CLASE , guardar como null ?
                    }

            } else { // Ahora para los demas structs

                // Recorro los atributos del struct
                for (EntradaAtributo a : struct.getAtributos().values()) {
                    if(a.getType().equals("Int")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .word 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\t\t" + struct.getName() + "_" + a.getName() + ": .word 0\n"));
                    }
                    if(a.getType().equals("Char") || a.getType().equals("Str")) {
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .asciiz " + " \n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\t\t" + struct.getName() + "_" + a.getName() + ": .asciiz " + " \n"));
                    }
                    if(a.getType().equals("Bool")){
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .word 1\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\t\t" + struct.getName() + "_" + a.getName() + ": .word 1\n"));
                    }
                    String[] palabras = a.getType().split(" ");
                    String isArray = palabras[0];
                    if(isArray.equals("Array")){
                        //this.data.put(v.getName(), "\t\t" + struct.getName() + "_"
                        //        + v.getName() + ": .space 0\n");
                        this.data.add(new AbstractMap.SimpleEntry<>(a.getName(),
                                "\t\t" + struct.getName() + "_" + a.getName() + ": .space 0\n"));
                    }
                    // FALTA CASO DE QUE VENGA ALGO DE TIPO CLASE , guardar como null ?
                }

                // Recorro los metodos del struct
                for (EntradaMetodo m : struct.getMetodos().values()) {

                    this.data.add(new AbstractMap.SimpleEntry<>(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName()));

                    // Recorro los parametros del metodo
                    for (EntradaParametro p : struct.getMetodos().get(m.getName()).getParametros().values()) {
                        if(p.getType().equals("Int")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .word 0\n"));

                        }
                        if(p.getType().equals("Char") || p.getType().equals("Str")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .asciiz " + " \n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .asciiz " + " \n"));
                        }
                        if(p.getType().equals("Bool")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 1\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .word 1\n"));
                        }
                        String[] palabras = p.getType().split(" ");
                        String isArray = palabras[0];
                        if(isArray.equals("Array")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .space 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(p.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + p.getName() + ": .space 0\n"));
                        }
                        // FALTA CASO DE QUE VENGA ALGO DE TIPO CLASE , guardar como null ?
                    }

                    // Recorro las variables del metodo
                    for (EntradaVariable v : struct.getMetodos().get(m.getName()).getVariables().values()) {
                        if(v.getType().equals("Int")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .word 0\n"));

                        }
                        if(v.getType().equals("Char") || v.getType().equals("Str")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .asciiz " + " \n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .asciiz " + " \n"));
                        }
                        if(v.getType().equals("Bool")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .word 1\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .word 1\n"));
                        }
                        String[] palabras = v.getType().split(" ");
                        String isArray = palabras[0];
                        if(isArray.equals("Array")){
                            //this.data.put(p.getName(), "\t\t" + struct.getName() + "_" + m.getName() + "_"
                            //        + p.getName() + ": .space 0\n");
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\t\t" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .space 0\n"));
                        }
                        // FALTA CASO DE QUE VENGA ALGO DE TIPO CLASE , guardar como null ?
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

                            // Para cada nodo genero codigo
                            s.generateNodeCode();

                        }
                    }
                }
            }
        }
    }

    private void generatePred(){

        // IO
        String finMetIn = "\tlw $ra, 4($sp) #ponemos el tope de la pila en $ra\n"
                + "\tadd $sp, $sp 8\n"
                + "\tlw  $fp, 0($sp)\n"
                + "\tjr $ra\n";

        String finMetOut = "\tlw $ra, 4($sp) #ponemos el tope de la pila en $ra\n"
                + "\tadd $sp, $sp 12\n"
                + "\tlw  $fp, 0($sp)\n"
                + "\tjr $ra\n";

        String iniMet = """
                        \tmove $fp, $sp #mueve el contenido de $sp a $fp
                        \tsw $ra, 0($sp) #copia el contenido de $ra a $sp (direccion de retorno)
                        \taddiu $sp, $sp, -4 #mueve el $sp 1 pos arriba
                        """;

        // Código MIPS para la función out_str
        // st fn out_str(Str s)->void: imprime el argumento

        text += "IO_out_str: \n";
        text += "# Asume que el argumento s está en $a0\n";
        text += "li $v0, 4  # Código de syscall para imprimir cadena\n";
        text += "syscall # Llamada al sistema para imprimir\n";
        text += "jr $ra  # Retorno\n";

    }

}
