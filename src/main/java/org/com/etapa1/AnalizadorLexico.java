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
    public void addToken( Token t){
        this.tokens.add(t);
    }
    //Extrae token

    public Token nextToken(){
        return tokens.remove();
    }

    public int countTokens() {
        return this.tokens.size();
    }

    private void analizarLinea(String linea, int numeroLinea) {

        for (int i = 0; i < linea.length(); i++) {

            // Obtener el carácter actual y el siguiente (si existe)
            char currentChar =  linea.charAt(i);
            String current = "" + currentChar;
            char nextChar = (i + 1 < linea.length()) ? linea.charAt(i + 1) : '\0';

            switch (currentChar) {
                case '(': // Parentesis abierto
                    addToken(new Token(numeroLinea, i, "par_open", current));
                    break;

                case ')': // Parentesis cerrado
                    addToken(new Token(numeroLinea, i, "par_close", current));
                    break;

                case '[': // Corchete abierto
                    addToken(new Token(numeroLinea, i, "cor_open", current));
                    break;

                case ']': // Corchete cerrado
                    addToken(new Token(numeroLinea, i, "cor_close", current));
                    break;

                case '}': // Llave abierta
                    addToken(new Token(numeroLinea, i, "braces_open", current));
                    break;

                case '{': // Llave cerrada
                    addToken(new Token(numeroLinea, i, "braces_close", current));
                    break;

                case ';': // Punto y coma
                    addToken(new Token(numeroLinea, i, "semicolon", current));
                    break;

                case ',': // Coma
                    addToken(new Token(numeroLinea, i, "comma", current));
                    break;

                case ':': // Dos puntos
                    addToken(new Token(numeroLinea, i, "colon", current));
                    break;

                case '.': // Punto
                    addToken(new Token(numeroLinea, i, "period", current));
                    break;

                case '*': // Multiplicacion
                    addToken(new Token(numeroLinea, i, "op_mult", current));
                    break;

                case '%': // Modulo
                    addToken(new Token(numeroLinea, i, "op_mod", current));
                    break;

                case '=': // Asignacion o Igualdad
                    if (nextChar == '=') { // Si el siguiente carácter es '=', es igualdad (==)
                        addToken(new Token(numeroLinea, i, "op_equal", current + nextChar));
                        i++;
                    } else { // Si no, es asignación (=)
                        addToken(new Token(numeroLinea, i, "op_assig", current));

                    }
                    break;

                case '<': // Menor o Menor-igual
                    if (nextChar == '=') { // Si el siguiente carácter es '=', es menor o igual (<=)
                        addToken(new Token(numeroLinea, i, "op_less_equal", current + nextChar));
                        i++;
                    } else { // Si no, es solo menor (<)
                        addToken(new Token(numeroLinea, i, "op_less", current));
                    }
                    break;

                case '>': // Mayor o Mayor_igual
                    if (nextChar == '=') { // Si el siguiente carácter es '=', es mayor o igual (>=)
                        addToken(new Token(numeroLinea, i, "op_greater_equal", current + nextChar));
                        i++;
                    } else { // Si no, es solo mayor (>)
                        addToken(new Token(numeroLinea, i, "op_greater", current));
                    }
                    break;

                case '!': // Not o Diferente
                    if (nextChar == '=') { // Si el siguiente carácter es '=', es diferencia (!=)
                        addToken(new Token(numeroLinea, i, "op_not_equal", current + nextChar));
                        i++;
                    } else { // Si no, es solo not (!)
                        addToken(new Token(numeroLinea, i, "op_not", current));
                    }
                    break;

                case '/': // Division o Comentario
                    if (nextChar == '?') { // Si el siguiente carácter es '?', es un comentario
                        i = linea.length();
                    } else { // Si no, es division
                        addToken(new Token(numeroLinea, i, "op_div", current));
                    }
                    break;

                case '&': // Operador AND
                    if (nextChar == '&') { // Si el siguiente carácter es '&', es un AND
                        addToken(new Token(numeroLinea, i, "op_and", current + nextChar));
                        i++;
                    } else { // Si no, es un error
                        throw new LexicalErrorException(numeroLinea, i + 1, "Se ha encontrado un solo '&' en la línea. Se esperaba un operador and (&&)");                    }
                    break;

                case '|': // Operador AND
                    if (nextChar == '|') { // Si el siguiente carácter es '|', es un OR
                        addToken(new Token(numeroLinea, i, "op_or", current + nextChar));
                        i++;
                    } else { // Si no, es un error
                        throw new LexicalErrorException(numeroLinea, i + 1, "Se ha encontrado un solo '|' en la línea. Se esperaba un operador or (||)");                    }
                    break;

                default:
                    // Caso por defecto, en caso de que no coincida con ningún caso anterior
                    break;
            }
        }

    }

}

