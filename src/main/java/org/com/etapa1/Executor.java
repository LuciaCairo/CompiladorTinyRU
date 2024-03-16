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

        try {
            BufferedWriter writerBuffer = null;

            if (printToFile) {
                writerBuffer = createdBuffer();
                writerBuffer.write("CORRECTO: ANALISIS LEXICO\n");
                writerBuffer.write(title);
            } else {
                System.out.print("CORRECTO: ANALISIS LEXICO\n");
                System.out.print(title);
            }

            l.analizarArchivo("C:\\Users\\Agustina\\Desktop\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru");

            if (printToFile) {
                printTokens(writerBuffer, l);
            } else {
                printTokensConsola(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        while (l.countTokens() > 0) {
            Token t = l.nextToken();
            String text = "| " + t.getName() +
                    " | " + t.getLexema() +
                    " | LINEA " + t.getLine() + " (COLUMNA " + t.getCol() + ") |\n";
            writerBuffer.write(text);
        }
        writerBuffer.close();
    }

    private static void printTokensConsola(AnalizadorLexico l) {
        while (l.countTokens() > 0) {
            Token t = l.nextToken();
            String text = "| " + t.getName() +
                    " | " + t.getLexema() +
                    " | LINEA " + t.getLine() + " (COLUMNA " + t.getCol() + ") |\n";
            System.out.print(text);
        }
    }
}

