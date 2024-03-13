package org.com.etapa1;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Executor {
    public static void main(String[] args) {

        AnalizadorLexico aL = new AnalizadorLexico();
        /* Variable para decidir si los tokens seran impresos por consola o por un archivo de salida
        - si se proporciona un 3er argumento: sera el nombre del archivo de salida .txt
        - si no hay 3er argumento, la salida sera impresa por pantalla */
        boolean printToFile = (args.length == 2);

        String title= "| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA)|\n";

        // Salida: archivo.txt
        if(!printToFile) {

            String rutaDirectorio = System.getProperty("user.dir");
            String rutaArchivo = rutaDirectorio + File.separator + "\\src\\main\\java\\org\\com\\etapa1\\salida.txt";

            try {
                System.out.print(rutaArchivo);
                File archivo = new File(rutaArchivo);
                FileWriter escritorArchivo = new FileWriter(archivo);
                BufferedWriter escritorBuffer = new BufferedWriter(escritorArchivo);
                escritorBuffer.write("CORRECTO: ANALISIS LEXICO\n");
                escritorBuffer.write(title);
                //escritorBuffer.close(); // ESTO NOSE SI ESTA BIEN QUE CIERRE

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else { // Salida: impresion por pantalla

            System.out.print("CORRECTO: ANALISIS LEXICO\n");
            System.out.print(title);
        }
        try {
            aL.analizarArchivo("C:\\Users\\Agustina\\Desktop\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru");

            Token t = aL.nextToken();
            System.out.println(t.getName());

        } catch (Exception e) {
            e.printStackTrace();

        }
        /*try {
            while(aL.hasNextToken()) {
                Token actualToken = aL.nextToken();
                linea = "| "+ actualToken.getpReservada() +
                        " | "+ actualToken.getValor()+
                        " | "+ actualToken.getFila() + " |\n";
                if(!printToFile) {
                    System.out.print(linea);

                }else {
                    impresora.write(linea);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();

        }finally {
            if(printToFile) {
                try {
                    impresora.close();
                }catch(Exception e2) {
                    e2.printStackTrace();
                }
            }
        }*/
    }
}

