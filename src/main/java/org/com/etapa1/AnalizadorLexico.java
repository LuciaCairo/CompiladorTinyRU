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
    public int isChar(String idChar, char nextChar) { //funcion para saber si es un char
        if (nextChar == '\'') {
            switch (idChar) {
                case "CharBlackSlash":

                    return 1;
                case "iterChar":
                    return 2;
            }

        }
        return 0;

    }
    private void analizarLinea(String linea, int numeroLinea) {
        String flag = "";
        String iterToken = "";
        for (int i = 0; i < linea.length(); i++) {

            // Obtener el carácter actual y el siguiente (si existe)
            char currentChar = linea.charAt(i);
            String current = "" + currentChar;

            char nextChar = (i + 1 < linea.length()) ? linea.charAt(i + 1) : '\0'; // '\0' indica el final de la cadena

            switch (currentChar) {
                case '\'':

                    if (currentChar == '\\'){//si despues de las ' viene /
                        flag = "CharBlackSlash";
                        i++;
                        break;
                    } else{
                        flag = "iterChar";
                        break;
                    }


                case '\0': //NULL
                    if (flag.equals("stringIter")){
                        //hay q largar excepción, porque seria q el string tenga null en el medio
                    }
                    break;
                case '"':
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    } else {

                        if (flag.equals("stringIter")) { //si recibo la ultima " y cierro el string

                            iterToken += current;
                            addToken(new Token(numeroLinea, i - iterToken.length() + 1, "str", iterToken));
                            flag = "";
                            iterToken = "";
                            break;

                        } else if (flag.equals("")) { //abro string
                            flag = "stringIter";
                            iterToken = current;
                        }

                        break;
                    }

                case '+': // operador suma
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    } else if (flag.equals("stringIter")) {//si se esta formando un string

                            iterToken += current;
                            break;
                    } else {
                            if (currentChar == '+' && nextChar == '+') {

                                addToken(new Token(numeroLinea, i, "op_incr", current + current));
                                i++;
                                break;
                            } else {
                                addToken(new Token(numeroLinea, i, "op_suma", current));
                                break;
                            }
                    }



                case '-': // operador resta
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        if (currentChar == '-' && nextChar == '-') {
                            addToken( new Token(numeroLinea, i, "op_decr", current + current));
                            i++;
                        } else {

                            addToken(new Token(numeroLinea, i, "op_resta", current));
                        }
                    }
                    break;
                case '(': // Parentesis abierto
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_open", current));
                        break;
                    }

                case ')': // Parentesis cerrado
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    } else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_close", current));
                        break;
                    }

                case '[': // Corchete abierto
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_open", current));
                        break;
                    }
                case ']': // Corchete cerrado
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_close", current));
                        break;
                    }
                case '}': // Llave abierta
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    } else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_open", current));
                        break;
                    }
                case '{': // Llave cerrada
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_close", current));
                        break;
                    }
                case ';': // Punto y coma
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "semicolon", current));
                        break;
                    }

                case ',': // Coma
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if ((flag.equals("stringIter"))) { //si se esta formando un string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "comma", current));
                        break;
                    }
                case ':': // Dos puntos
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) {//si se esta formando el string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "colon", current));
                        break;
                    }
                case '.': // Punto
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    } else if (flag.equals("stringIter")) { //si se esta formando string

                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "period", current));
                        break;
                    }
                case '/':
                    if(isChar(flag,nextChar) == 1) { // si es un char
                        addToken(new Token(numeroLinea, i + 1, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (isChar(flag, nextChar) == 2) {
                        addToken(new Token(numeroLinea, i, "char", current));
                        flag = "";
                        i++;
                        break;
                    }else if (flag.equals("stringIter")) { //si se esta formando string

                        iterToken += current;
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

                            if (currentChar >= '0' && currentChar <= '9') {
                                if(isChar(flag,nextChar) == 1) { // si es un char
                                    addToken(new Token(numeroLinea, i + 1, "char", current));
                                    flag = "";
                                    i++;
                                    break;
                                }else if (isChar(flag, nextChar) == 2) {
                                    addToken(new Token(numeroLinea, i, "char", current));
                                    flag = "";
                                    i++;
                                    break;
                                }else if (flag.equals("stringIter")) { //si se esta formando string

                                    iterToken += current;
                                    break;
                                } else {
                                    if ((!(nextChar >= '0' && nextChar <= '9')) || nextChar == '\0') {
                                        iterToken += current;
                                        addToken(new Token(numeroLinea, i - iterToken.length() + 1, "int", iterToken));
                                        flag = "";
                                        iterToken = "";
                                        break;
                                    } else {
                                        if (flag == "") {
                                            flag = "int";
                                            iterToken = current;
                                            break;
                                        } else if (flag == "int") {
                                            iterToken += current;
                                            break;
                                        }
                                        break;
                                    }

                                }
                            } else if (currentChar >= 65 && currentChar <= 90){ //Letras mayuscula en ASCII
                                if(isChar(flag,nextChar) == 1) { // si es un char
                                    addToken(new Token(numeroLinea, i + 1, "char", current));
                                }else if (isChar(flag, nextChar) == 2) {
                                    addToken(new Token(numeroLinea, i, "char", current));

                                }
                                flag = "";
                                i++;
                                break;

                            } else if (currentChar >= 97 && currentChar <= 122){ //Letras minusculas

                                if(isChar(flag,nextChar) == 1) { // si es un char
                                    addToken(new Token(numeroLinea, i + 1, "char", current));
                                }else if (isChar(flag, nextChar) == 2) {
                                    addToken(new Token(numeroLinea, i, "char", current));

                                }
                                flag = "";
                                i++;
                                break;

                            }


            }


            // Implementa aquí la lógica para analizar los lexemas en la línea y agregar tokens a la lista
            // Puedes utilizar métodos de manipulación de cadenas y comparaciones
            // Añade instancias de la clase Token a la lista de tokens
        }


    }
}


