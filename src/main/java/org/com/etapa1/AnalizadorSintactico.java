package org.com.etapa1;

import java.io.File;
import java.io.IOException;

public class AnalizadorSintactico {

    private static AnalizadorLexico l;
    private static Token currentToken;

    public static void main(String[] args) {
        /*if (args.length < 1) {
            System.out.println("ERROR: Debe proporcionar el nombre del archivo fuente.ru como argumento");
            System.out.println("Uso: java -jar etapa1.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]");
            return;
        }*/

        //String input = args[0];
        String input = "C:\\Users\\Luci\\Documents\\Ciencias de la Computacion\\Compiladores\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru";

        // Verificar existencia del archivo
        File file = new File(input);
        if (!file.exists()) {
            System.out.println("ERROR: El archivo fuente '" + input + "' no existe.");
            return;
        }

        // Verificar si el archivo no está vacío
        if (file.length() == 0) {
            System.out.println("ERROR: El archivo fuente '" + input + "' está vacío.");
            return;
        }

        // Verificar extensión del archivo
        if (!input.endsWith(".ru")) {
            System.out.println("ERROR: El archivo fuente debe tener la extensión '.ru'.");
            return;
        }

        l = new AnalizadorLexico();

        try {
            l.analyzeFile(input);
            currentToken = l.nextToken();
            // Comenzar el análisis sintáctico desde el símbolo inicial ⟨program⟩
            program();

            System.out.println("CORRECTO: ANALISIS SINTACTICO \n");

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        }
    }

    // Método para emparejar el token actual con un token esperado
    private static void match(String expectedToken) {
        if (currentToken.getLexema().equals(expectedToken) ||
                currentToken.getName().equals(expectedToken)) {
            advance();
        } else {
            System.out.println("Error de sintaxis. Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getLexema());
            System.exit(1);
            // ACA VA UNA EXCEPCION EN REALIDAD
        }
    }

    // Método para avanzar al siguiente token
    private static void advance() {
        if (l.countTokens() <= 0){ // No hay mas tokens
            // Aca nose bien que deberia pasar
            // Excepcion ??
        } else {
            currentToken = l.nextToken();
        }
    }

    private static void program() {
        if (currentToken.getLexema().equals("struct") || currentToken.getLexema().equals("impl")){
            definiciones();
            start(); // No se si va start
        } else {
            start();
        } // Falta EXCEPCION !!
    }

    private static void start() {
        match("start");
        bloqueMetodo();
    }

    private static void definiciones() {
        if (currentToken.getLexema().equals("struct")) {
            struct();
            definiciones1(); // Aca nose si tambien se llama a definiciones1
        } else if (currentToken.getLexema().equals("impl")) {
            impl();
            definiciones1(); // Aca nose si tambien se llama a definiciones1
        } else {
            System.out.println("Error Sintactico. Se esperaba 'struct' o 'impl'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void definiciones1() {
        if (currentToken.getLexema().equals("struct") || currentToken.getLexema().equals("impl")) {
            definiciones();
        } /*else if(// no entiendo que va aca . segun chat: currentToken.getLexema().equals("$") ){
            return;
        } else{
            System.out.println("Error Sintactico. Se esperaba 'struct' o 'impl'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }*/
    }

    private static void struct() {
        match("struct");
        match("struct_name");
        struct1();
    }

    private static void struct1() {
        if (currentToken.getLexema().equals(":")) {
            herencia();
            match("{");
        } else if (currentToken.getLexema().equals("{")) {
            match("{");
            struct2();
        } else {
            System.out.println("Error Sintactico. Se esperaba ':' o '{'");
            System.exit(1); // EXCEPCION!!!
            // Lanzar una excepción
        }
    }

    private static void struct2() {
        if (currentToken.getLexema().equals("pri") ||
                currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("struct_name") ||
                currentToken.getLexema().equals("Array")) {
            atributos();
            match("}");
        } else if (currentToken.getLexema().equals("}")) {
            match("}");
        } else {
            System.out.println("Error Sintactico. Se esperaba..");
            System.exit(1); // EXCEPCION!!!
        }
    }


    private static void bloqueMetodo() {
        match("{");
        bloqueMetodo1();
    }

    private static void bloqueMetodo1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            declaraciones();
            bloqueMetodo2();
        } else if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("(")){
            sentencias();
            match("}");
        } else if(currentToken.getLexema().equals("}")) {
            match("}");
        } else{
            System.out.println("Error Sintactico. Se esperaba ':' o '{'");
            System.exit(1); // EXCEPCION!!!
            // Lanzar una excepción en lugar de simplemente imprimir un mensaje de error
        }
    }

    private static void atributos() {
        atributo();
        atributos1();
    }

    private static void sentencias() {
    }

    private static void declaraciones() {
    }

    private static void atributo() {
    }

    private static void atributos1() {
    }

    private static void impl() {
        match("impl");
        match("IdStruct");
        match("{");
        miembros();
        match("}");
    }

    private static void miembros() {
        miembro();
        miembros1();
    }

    private static void miembros1() {
        if (currentToken.getLexema().equals("pri")
                || currentToken.getLexema().equals("fn")
                || currentToken.getLexema().equals(".")) {
            miembros();
        } /*else if(// no entiendo que va aca . segun chat: currentToken.getLexema().equals("$") ){
            return;
        } else{
            System.out.println("Error Sintactico. Se esperaba 'struct' o 'impl'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }*/
    }

    private static void herencia() {
        // Analiza la herencia de la estructura
    }

    private static void miembro() {
        // Analiza un miembro de la estructura (método o constructor)
    }

    private static void constructor() {
        // Analiza la definición de un constructor
    }

    private static void metodo() {
        // Analiza la definición de un método
    }

    private static void bloqueMetodo2() {
        // Analiza el contenido del bloque de un método
    }







}
