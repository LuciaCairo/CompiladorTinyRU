package org.com.etapa1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AnalizadorLexico {

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
    public Token nextToken(){
        return tokens.remove();
    }

    public int countTokens() {
        return this.tokens.size();
    }
    public int isChar(String idChar, char nextChar,int fila ,int columna, String current) { //funcion para saber si es un char
        if (nextChar == '\'') {
            if (idChar == "CharBlackSlash") {
                addToken(new Token(fila, columna + 1, "char", current));
            } else if (idChar == "iterChar") {
                addToken(new Token(fila, columna, "char", current));
            }

        } else{
            throw new LexicalErrorException(fila, columna, "Caracter mal formado. Se esperaba fin de caracter (') despues de " + current );
        }
        return 0;

    }
    private void analizarLinea(String linea, int numeroLinea) {
        String flag = ""; // Bandera para indicar que se esta guardando
        String iterToken = "";
        int countStr= 0;

        for (int i = 0; i < linea.length(); i++) {
            // Obtener el carácter actual y el siguiente (si existe)
            char currentChar = linea.charAt(i);
            String current = "" + currentChar;
            char nextChar = (i + 1 < linea.length()) ? linea.charAt(i + 1) : '\0';

            switch (currentChar) {
                case '\'':
                    if (flag.equals("stringIter")) {//si se esta formando un string
                        countStr++;
                        iterToken += current;

                        break;
                    }else if (nextChar == '\\') {//si despues de las ' viene /
                        flag = "CharBlackSlash";
                        i++;
                        break;
                    } else {
                        flag = "iterChar";
                        break;
                    }


                case '\\': //NULL

                    if (flag.equals("stringIter") && nextChar == '0') {
                        //hay q largar excepción, porque seria q el string tenga null en el medio
                        throw new LexicalErrorException(numeroLinea, i, "Caracter invalido. Los Str no permiten caracter null");
                    }
                    break;
                case '"': //si comienza un string o quiero agregar un char
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else {
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


                case '+': // Operador suma
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) {//si se esta formando un string
                        countStr++;
                        iterToken += current;

                        break;
                    } else {
                        if (nextChar == '+') { // Si el siguiente carácter es '+', es incremento (++)
                            addToken(new Token(numeroLinea, i, "op_incr", current + current));
                            i++;
                        } else { // Si no, es suma (+)
                            addToken(new Token(numeroLinea, i, "op_sum", current));
                        }
                    }
                    break;

                case '-': // Operador resta
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) { //Si se esta formando un string
                        iterToken += current;

                    } else {
                        if (nextChar == '-') { // Si el siguiente carácter es '-', es decremento (--)
                            addToken(new Token(numeroLinea, i, "op_decr", current + current));
                            i++;
                        } else {
                            if (nextChar == '>') { // Si el siguiente carácter es '>', es retorno de func (->)
                                addToken(new Token(numeroLinea, i, "ret_func", current + nextChar));
                                i++;
                            } else { // Si no, es resta (-)
                                addToken(new Token(numeroLinea, i, "op_rest", current));
                            }
                        }
                    }
                    break;

                case '(': // Parentesis abierto
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_open", current));
                    }
                    break;

                case ')': // Parentesis cerrado
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) { //si se esta formando un string
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_close", current));
                    }
                    break;

                case '[': // Corchete abierto
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) { //si se esta formando un string
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_open", current));
                    }
                    break;

                case ']': // Corchete cerrado
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) { //si se esta formando un string

                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_close", current));
                    }
                    break;


                case '{': // Llave abierta
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) { //si se esta formando un string
                        iterToken += current;
                        break;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_close", current));
                        break;
                    }
                case '}': // Llave cerrada
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_close", current));
                    }
                    break;

                case ';': // Punto y coma
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "semicolon", current));
                    }
                    break;

                case ',': // Coma
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if ((flag.equals("stringIter"))) { //si se esta formando un string

                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "comma", current));
                    }
                    break;

                case ':': // Dos puntos
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {//si se esta formando el string

                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "colon", current));
                    }
                    break;

                case '.': // Punto
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) { //si se esta formando string

                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "period", current));
                    }
                    break;

                case '/': // Division o Comentario
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '?') { // Si el siguiente carácter es '?', es un comentario
                            i = linea.length(); // Se ignora el resto de la linea
                        } else {
                            if (nextChar == 'n') { // Si el siguiente carácter es 'n', es un salto (/n)
                                addToken(new Token(numeroLinea, i, "new_line", current + nextChar));
                                i++;
                            } else {
                                if (nextChar == '0') { // Si el siguiente carácter es '0', es un null (/0)
                                    addToken(new Token(numeroLinea, i, "op_null", current + nextChar));
                                    i++;
                                } else { // Si no, es division (/)
                                    addToken(new Token(numeroLinea, i, "op_div", current));
                                }
                            }
                        }
                    }
                    break;

                case '*': // Multiplicacion
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "op_mult", current));
                    }
                    break;

                case '%': // Modulo
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "op_mod", current));
                    }
                    break;

                case '=': // Asignacion o Igualdad
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '=') { // Si el siguiente carácter es '=', es igualdad (==)
                            addToken(new Token(numeroLinea, i, "op_equal", current + nextChar));
                            i++;
                        } else { // Si no, es asignación (=)
                            addToken(new Token(numeroLinea, i, "op_assig", current));
                        }
                    }
                    break;

                case '<': // Menor o Menor-igual
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '=') { // Si el siguiente carácter es '=', es menor o igual (<=)
                            addToken(new Token(numeroLinea, i, "op_less_equal", current + nextChar));
                            i++;
                        } else { // Si no, es solo menor (<)
                            addToken(new Token(numeroLinea, i, "op_less", current));
                        }
                    }
                    break;

                case '>': // Mayor o Mayor_igual
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '=') { // Si el siguiente carácter es '=', es mayor o igual (>=)
                            addToken(new Token(numeroLinea, i, "op_greater_equal", current + nextChar));
                            i++;
                        } else { // Si no, es solo mayor (>)
                            addToken(new Token(numeroLinea, i, "op_greater", current));
                        }
                    }
                    break;

                case '!': // Not o Diferente
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '=') { // Si el siguiente carácter es '=', es diferencia (!=)
                            addToken(new Token(numeroLinea, i, "op_not_equal", current + nextChar));
                            i++;
                        } else { // Si no, es solo not (!)
                            addToken(new Token(numeroLinea, i, "op_not", current));
                        }
                    }
                    break;

                case '&': // Operador AND
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '&') { // Si el siguiente carácter es '&', es un AND
                            addToken(new Token(numeroLinea, i, "op_and", current + nextChar));
                            i++;
                        } else { // Si no, es un error
                            throw new LexicalErrorException(numeroLinea, i + 1, "Se ha encontrado un solo '&' en la línea. Se esperaba un operador and (&&)");
                        }
                    }
                    break;

                case '|': // Operador AND
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '|') { // Si el siguiente carácter es '|', es un OR
                            addToken(new Token(numeroLinea, i, "op_or", current + nextChar));
                            i++;
                        } else { // Si no, es un error
                            throw new LexicalErrorException(numeroLinea, i + 1, "Se ha encontrado un solo '|' en la línea. Se esperaba un operador or (||)");
                        }
                    }
                    break;

                case '?':


                default:

                    if (currentChar >= '0' && currentChar <= '9') {
                        if(flag == "CharBlackSlash" || flag == "iterChar"){
                            isChar(flag,nextChar,numeroLinea,i,current);
                            flag = "";
                            i++;

                        } else if (flag.equals("stringIter")) { //si se esta formando string

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

                                } else if (flag == "int") {
                                    iterToken += current;
                                }

                            }
                            break;
                        }
                    } else if (currentChar >= 65 && currentChar <= 90) { //Letras mayuscula en ASCII
                        if(flag == "CharBlackSlash" || flag == "iterChar"){

                            isChar(flag,nextChar,numeroLinea,i,current);
                            flag = "";
                            i++;

                        }

                        break;

                    } else if (currentChar >= 97 && currentChar <= 122) { //Letras minusculas

                        if(flag == "CharBlackSlash" || flag == "iterChar"){
                            //if (currentChar == 116){

                            //} else {
                                isChar(flag,nextChar,numeroLinea,i,current);
                                flag = "";
                                i++;
                            //}


                        }
                        break;
                        //si me viene un caracter chino
                    } else if ((currentChar >= '\u4E00' && currentChar <= '\u9FFF') || (currentChar >= '\u3400' && currentChar <= '\u4DBF')){
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido ' " + current + "'" );
                      // alfabeto Hangeul
                    } else if ((currentChar >= '\uAC00' && currentChar <= '\uD7A3') || (currentChar >= '\u3131' && currentChar <= '\u318E')){
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido ' " + current + "'" );
                        //alfabeto griego
                    } else if ((currentChar>= '\u0391' && currentChar <= '\u03A1') || (currentChar >= '\u03A3' && currentChar <= '\u03A9') ||
                        (currentChar >= '\u03B1' && currentChar <= '\u03C1') || (currentChar >= '\u03C3' && currentChar <= '\u03C9')) {
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido ' " + current + "'" );
                    } else if(currentChar == '@'){
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido ' " + current + "'" );

                    }



            }
        }
    }
}



