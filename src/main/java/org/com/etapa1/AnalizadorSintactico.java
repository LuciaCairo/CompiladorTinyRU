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
            // Verificar si se ha alcanzado el final del archivo
            //match("EOF");
            System.out.println("CORRECTO: ANALISIS SINTACTICO \n");

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        }
    }

    private static void program() {
        start();
    }

    private static void start() {
        match("start");
        // Aca va el código para analizar el resto de la producción ⟨Start⟩
    }

    // Método para emparejar el token actual con un token esperado
    private static void match(String expectedToken) {
        if (currentToken.getLexema().equals(expectedToken)) {
            advance();
        } else {
            System.out.println("Error de sintaxis. Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getName());
            System.exit(1);
            // ACA VA UNA EXCEPCION EN REALIDAD
        }
    }

    // Método para avanzar al siguiente token
    private static void advance() {
        currentToken = l.nextToken();
    }

    /*private static void printTokensConsola(AnalizadorLexico l) {
        int n = l.countTokens();
        while (n > 0) {
            Token t = l.nextToken();
            if(t.getName() == "EOF") {
                n = 0;
            } else {
                // ACA VAMOS RECIBIENDO LOS TOKENS Y LLAMANDO A LOS METODOS
                String text = "TOKEN: " + t.getName()  + "\n";
                System.out.print(text);
                n = l.countTokens();
            }
        }
    }*/
}
