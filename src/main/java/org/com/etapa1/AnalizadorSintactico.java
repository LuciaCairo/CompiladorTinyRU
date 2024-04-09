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
        if (currentToken.getLexema().equals(expectedToken)) {
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
            // excepcion ??
        } else {
            currentToken = l.nextToken();
        }
    }

    // NOSE SI ESTA BIEN DEFINIDA ESTA FUNCION
    private static void program() {
        if (currentToken.getLexema().equals("struct") || currentToken.getLexema().equals("impl")){
            definiciones();
            start();
        } else {
            start();
        }

    }

    private static void definiciones() {

    }

    private static void start() {
    }


}
