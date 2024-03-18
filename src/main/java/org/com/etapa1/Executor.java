package org.com.etapa1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Executor {
    public static void main(String[] args) {
        AnalizadorLexico l = new AnalizadorLexico();
        boolean printToFile = (args.length == 2);

        BufferedWriter writerBuffer = null;

        try {
            if (printToFile) {
                writerBuffer = createdBuffer();
            }

            l.analizarArchivo("C:\\Users\\Agustina\\Desktop\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru");

            if (printToFile) {
                printTokens(writerBuffer, l);
            } else {
                printTokensConsola(l);
            }

        } catch (LexicalErrorException e) {
            if (printToFile && writerBuffer != null) {
                try {
                    writerBuffer.write("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |\n");
                    writerBuffer.write( "| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
                System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");
            }

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
        while (l.countTokens() > 0) {
            Token t = l.nextToken();
            String text = "| " + t.getName() +
                    " | " + t.getLexema() +
                    " | LINEA " + t.getLine() + " (COLUMNA " + t.getCol() + ") |\n";
            writerBuffer.write(text);
        }
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
