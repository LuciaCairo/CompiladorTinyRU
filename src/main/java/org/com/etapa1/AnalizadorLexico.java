package org.com.etapa1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AnalizadorLexico {

    //private Queue<Token> tokens;
    private Queue<Token> tokens;
    public AnalizadorLexico() {
        this.tokens = new LinkedList<>();
    }

    public Queue<Token> analizarArchivo(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 1;

            while ((linea = br.readLine()) != null) {
                System.out.println(" ");
                analizarLinea(linea, numeroLinea);


                numeroLinea++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }
    //Agregar token
    public void addToken( Token t){
        this.tokens.add(t);
    }
    //Extrae token

    public Token nextToken(){
        return tokens.remove();
    }


     private void analizarLinea(String linea, int numeroLinea) {

        for (int i = 0; i<linea.length(); i++){

            String current = "";
            String nextCurrent = "";
            // Verificar si la línea es un comentario de una sola línea
            if (linea.charAt(i) == '/' ){
                // Es un comentario, ignorar la línea
                if (linea.length() != i+1 && linea.charAt(i+1) == '?') {
                    return;
                }
                //sino es un op

                Token token = new Token(numeroLinea,i,"op_div", linea.charAt(i));
                addToken(token);

             /*else {
                if (linea.charAt(i) == '*'){
                    Token token = new Token(numeroLinea,i,"t_mult", linea.charAt(i));
                }
                if (linea.charAt(i) == '%'){
                    Token token = new Token(numeroLinea,i,"t_porc", linea.charAt(i));
                }
                if (linea.charAt(i) == '+'){
                    Token token = new Token(numeroLinea,i,"t_suma", linea.charAt(i));
                }
                if (linea.charAt(i) == '-'){
                    Token token = new Token(numeroLinea,i,"t_resta", linea.charAt(i));
                }
                if (linea.charAt(i) == ':'){
                    Token token = new Token(numeroLinea,i,"t_porc", linea.charAt(i));
                } */
            }


        }


        // Implementa aquí la lógica para analizar los lexemas en la línea y agregar tokens a la lista
        // Puedes utilizar métodos de manipulación de cadenas y comparaciones
        // Añade instancias de la clase Token a la lista de tokens
    }


}

