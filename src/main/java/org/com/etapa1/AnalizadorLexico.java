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
                analizarLinea(linea, numeroLinea);
                numeroLinea++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    //Agregar token
    public void addToken(Token t) {
        this.tokens.add(t);
    }
    //Extrae token

    public Token nextToken() {
        return tokens.remove();
    }

    public int countTokens() {
        return this.tokens.size();
    }

    private void analizarLinea(String linea, int numeroLinea) {
        String var1 = "";
        String var2 = "";
        for (int i = 0; i < linea.length(); i++) {

            // Obtener el carácter actual y el siguiente (si existe)
            char currentChar = linea.charAt(i);
            String current = "" + currentChar;

            char nextChar = (i + 1 < linea.length()) ? linea.charAt(i + 1) : '\0'; // '\0' indica el final de la cadena

            switch (currentChar) {


                case '"':

                    if (var1.equals("lit_comillas_abiertas")) { //si recibo la ultima " y cierro el string

                        var2 += current;
                        addToken(new Token(numeroLinea, i - var2.length() + 1, "str", var2));
                        var1 = "";
                        var2 = "";
                        break;

                    } else if (var1.equals("")) { //abro string
                        var1 = "lit_comillas_abiertas";
                        var2 = current;
                    }

                    break;

                case '+': // operador suma
                    if (var1.equals("lit_comillas_abiertas")) {//si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        if (currentChar == '+' && nextChar == '+') {
                            Token token = new Token(numeroLinea, i, "op_incr", current + current);
                            addToken(token);
                            i++;
                        } else {

                            Token token = new Token(numeroLinea, i, "op_suma", current);
                            addToken(token);
                        }
                    }

                    break;
                case '-': // operador resta
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        if (currentChar == '-' && nextChar == '-') {
                            Token token = new Token(numeroLinea, i, "op_decr", current + current);
                            addToken(token);
                            i++;
                        } else {
                            Token token = new Token(numeroLinea, i, "op_resta", current);
                            addToken(token);
                        }
                    }
                    break;
                case '(': // Parentesis abierto

                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_open", current));
                        break;
                    }

                case ')': // Parentesis cerrado
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_close", current));
                        break;
                    }

                case '[': // Corchete abierto
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_open", current));
                        break;
                    }
                case ']': // Corchete cerrado
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_close", current));
                        break;
                    }
                case '}': // Llave abierta
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_open", current));
                        break;
                    }
                case '{': // Llave cerrada
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_close", current));
                        break;
                    }
                case ';': // Punto y coma
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "semicolon", current));
                        break;
                    }

                case ',': // Coma
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando un string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "comma", current));
                        break;
                    }
                case ':': // Dos puntos
                    if (var1.equals("lit_comillas_abiertas")) {//si se esta formando el string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "colon", current));
                        break;
                    }
                case '.': // Punto
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando string

                        var2 += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "period", current));
                        break;
                    }
                case '/':
                    if (var1.equals("lit_comillas_abiertas")) { //si se esta formando string

                        var2 += current;
                        break;
                    } else {
                        if (currentChar == '/' && nextChar == '?') {
                            return;
                        }
                    }
                    Token token = new Token(numeroLinea, i, "op_div", current);
                    addToken(token);
                    break;
                default:
                            // Caso por defecto, en caso de que no coincida con ningún caso anterior
                            //si viene un numero int
                            if (currentChar > '1' && currentChar < '9') {


                            }
                            break;

                    }
            }


            // Implementa aquí la lógica para analizar los lexemas en la línea y agregar tokens a la lista
            // Puedes utilizar métodos de manipulación de cadenas y comparaciones
            // Añade instancias de la clase Token a la lista de tokens
        }


    }
}


