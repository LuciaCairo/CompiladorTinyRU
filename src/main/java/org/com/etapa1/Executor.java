package org.com.etapa1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Executor {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("ERROR: Debe proporcionar el nombre del archivo fuente.ru como argumento");
            System.out.println("Uso: java -jar etapa1.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]");
            return;
        }

        String input = args[0];

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

        String output = null;

        // Verificamos si se proporciona un archivo de salida
        if (args.length >= 2) {
            output = args[1];
        }

        AnalizadorLexico l = new AnalizadorLexico();
        boolean printToFile = (args.length == 2);
        String title = "| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA)|\n";

        BufferedWriter writerBuffer = null;

        try {
            if (printToFile) {
                writerBuffer = createdBuffer(output);
            }

            l.analyzeFile(input);

            if (printToFile) {
                writerBuffer.write("CORRECTO: ANALISIS LEXICO\n");
                writerBuffer.write(title);
                printTokens(writerBuffer, l);
            } else {
                System.out.print("CORRECTO: ANALISIS LEXICO\n");
                System.out.print(title);
                printTokensConsola(l);
            }

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (writerBuffer != null) {
                    writerBuffer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static BufferedWriter createdBuffer(String output) throws IOException {
        FileWriter writerArchivo = new FileWriter(output);
        return new BufferedWriter(writerArchivo);
    }

    private static void printTokens(BufferedWriter writerBuffer, AnalizadorLexico l) throws IOException {
        int n = l.countTokens();
        while ( n > 0) {
            Token t = l.nextToken();
            if(t.getName() == "EOF") {
                n = 0;
            } else {
                String text = "| " + t.getName() +
                        " | " + t.getLexema() +
                        " | LINEA " + t.getLine() + " (COLUMNA " + t.getCol() + ") |\n";
                writerBuffer.write(text);
                n = l.countTokens();
            }
        }
    }

    private static void printTokensConsola(AnalizadorLexico l) {
        int n = l.countTokens();
        while (n > 0) {
            Token t = l.nextToken();
            if(t.getName() == "EOF") {
                n = 0;
            } else {
                String text = "| " + t.getName() +
                        " | " + t.getLexema() +
                        " | LINEA " + t.getLine() + " (COLUMNA " + t.getCol() + ") |\n";
                System.out.print(text);
                n = l.countTokens();
            }
        }
    }
}

