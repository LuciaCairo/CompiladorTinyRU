package org.com.etapa1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Executor {
    public static void main(String[] args) {
        AnalizadorLexico l = new AnalizadorLexico();
        boolean printToFile = (args.length == 2);
        String title = "| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA)|\n";

        BufferedWriter writerBuffer = null;

        try {
            if (printToFile) {
                writerBuffer = createdBuffer();
            }

            l.analizarArchivo("C:\\Users\\Luci\\Documents\\Ciencias de la Computacion\\Compiladores\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru");

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

    private static BufferedWriter createdBuffer() throws IOException {
        String router = getRoute();
        FileWriter writerArchivo = new FileWriter(router);
        return new BufferedWriter(writerArchivo);
    }

    private static String getRoute() {
        String routeDirectorio = System.getProperty("user.dir");
        return routeDirectorio + File.separator + "\\src\\main\\java\\org\\com\\etapa1\\salida.txt";
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
