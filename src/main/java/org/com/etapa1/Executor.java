package org.com.etapa1;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Executor {
    public static void main(String[] args) {

        // LEER UN ARCHIVO CON RUTA (HAY QUE MODIFICAR PARA QUE SEA POR PARAMETRO)
        String rutaArchivo = "C:\\Users\\Luci\\Documents\\Ciencias de la Computacion\\Compiladores\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru";

        try {

            // Crea un objeto File para el archivo.
            File archivo = new File(rutaArchivo);

            // Crea un objeto FileReader para el archivo.
            FileReader lectorArchivo = new FileReader(archivo);

            // Crea un objeto BufferedReader para el archivo.
            BufferedReader lectorBuffer = new BufferedReader(lectorArchivo);

            // Lee cada línea del archivo y la imprime en la consola.
            String linea;
            int numLinea = 1;
            while ((linea = lectorBuffer.readLine()) != null) {
                System.out.print("Linea " + numLinea + "\n");
                numLinea++;
                System.out.println(linea);
            }

            // Cierra el lector de archivos.
            lectorBuffer.close();

        } catch (IOException e) {

            // Maneja la excepción de E/S.
            e.printStackTrace();

        }

        // SALIDA: UN ARCHIVO ,txt (HAY QUE MODIFICAR PARA QUE LA SALIDA SEAN LOS TOKENS)
        // Ruta al archivo que deseas crear.
        String ruta = "C:\\Users\\Luci\\Documents\\Ciencias de la Computacion\\Compiladores\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\salida.txt";

        // Contenido que deseas escribir en el archivo.
        String contenido = "Este es el contenido del archivo.";

        try {

            // Crea un objeto File para el archivo.
            File archivo = new File(ruta);

            // Crea un objeto FileWriter para el archivo.
            FileWriter escritorArchivo = new FileWriter(archivo);

            // Crea un objeto BufferedWriter para el archivo.
            BufferedWriter escritorBuffer = new BufferedWriter(escritorArchivo);

            // Escribe el contenido en el archivo.
            escritorBuffer.write(contenido);

            // Cierra el escritor de archivos.
            escritorBuffer.close();

        } catch (IOException e) {

            // Maneja la excepción de E/S.
            e.printStackTrace();

        }



    }
}

