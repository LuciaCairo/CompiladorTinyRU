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

    private void analizarLinea(String linea, int numeroLinea) {
        String flag = ""; // Bandera para indicar que se esta guardando
        String iterToken = "";

        for (int i = 0; i < linea.length(); i++) {
            // Obtener el carácter actual y el siguiente (si existe)
            char currentChar = linea.charAt(i);
            String current = "" + currentChar;
            char nextChar = (i + 1 < linea.length()) ? linea.charAt(i + 1) : '\0';

            switch (currentChar) {

                case '"': // String
                    if (flag.equals("stringIter")) { // Recibo la ultima " y cierro el string
                        iterToken += current;
                        addToken(new Token(numeroLinea, i - iterToken.length() + 1, "str", iterToken));
                        flag = "";
                        iterToken = "";
                        break;

                    } else if (flag.equals("")) { // Recibo un " entonces comienzo a guardar el string
                        flag = "stringIter"; // Marco la bandera para saber que se esta guardando un string
                        iterToken = current;
                    }
                    break;

                case '+': // Operador suma
                    if (flag.equals("stringIter")) { //Si se esta formando un string
                        iterToken += current;

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
                    if (flag.equals("stringIter")) { //Si se esta formando un string
                        iterToken += current;

                    } else {
                        if (nextChar == '-') { // Si el siguiente carácter es '-', es decremento (--)
                            addToken(new Token(numeroLinea, i, "op_decr", current + current));
                            i++;
                        } else {
                            if (nextChar == '>') { // Si el siguiente carácter es '>', es retorno de func (->)
                                addToken(new Token(numeroLinea, i, "ret_func", current + nextChar));
                                i++;
                            } else{ // Si no, es resta (-)
                                    addToken(new Token(numeroLinea, i, "op_rest", current));
                            }
                        }
                    }
                    break;

                case '(': // Parentesis abierto
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_open", current));
                    }
                    break;

                case ')': // Parentesis cerrado
                    if (flag.equals("stringIter")) { //si se esta formando un string
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "par_close", current));
                    }
                    break;

                case '[': // Corchete abierto
                    if (flag.equals("stringIter")) { //si se esta formando un string
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_open", current));
                    }
                    break;

                case ']': // Corchete cerrado
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "cor_close", current));
                    }
                    break;

                case '{': // Llave abierta
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_open", current));
                    }
                    break;

                case '}': // Llave cerrada
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_close", current));
                    }
                    break;

                case ';': // Punto y coma
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "semicolon", current));
                    }
                    break;

                case ',': // Coma
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "comma", current));
                    }
                    break;

                case ':': // Dos puntos
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "colon", current));
                    }
                    break;

                case '.': // Punto
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "period", current));
                    }
                    break;

                case '/': // Division o Comentario
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        if (nextChar == '?') { // Si el siguiente carácter es '?', es un comentario
                            i = linea.length(); // Se ignora el resto de la linea
                        } else {
                            if (nextChar == 'n') { // Si el siguiente carácter es 'n', es un salto (/n)
                                addToken(new Token(numeroLinea, i, "new_line", current + nextChar));
                                i++;
                            } else{
                                if (nextChar == '0') { // Si el siguiente carácter es '0', es un null (/0)
                                    addToken(new Token(numeroLinea, i, "op_null", current + nextChar));
                                    i++;
                                } else{ // Si no, es division (/)
                                    addToken(new Token(numeroLinea, i, "op_div", current));
                                }
                            }
                        }
                    }
                    break;

                case '*': // Multiplicacion
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                    addToken(new Token(numeroLinea, i, "op_mult", current));
                    }
                    break;

                case '%': // Modulo
                    if (flag.equals("stringIter")) {
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "op_mod", current));
                    }
                    break;

                case '=': // Asignacion o Igualdad
                    if (flag.equals("stringIter")) {
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
                    if (flag.equals("stringIter")) {
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
                    if (flag.equals("stringIter")) {
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
                    if (flag.equals("stringIter")) {
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
                    if (flag.equals("stringIter")) {
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
                    if (flag.equals("stringIter")) {
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

                default:

                    // Identificadores
                    if (currentChar >= 97 && currentChar <= 122){ // Letras minusculas en ASCII
                        if (flag.equals("stringIter")) {
                            iterToken += current;
                        } else {
                            if (flag == "") {
                                if (nextChar == ' ' || i + 1 == linea.length()) {
                                    addToken(new Token(numeroLinea, i - iterToken.length() + 1, "id", current));
                                } else {
                                    flag = "identifier";
                                    iterToken += current;
                                }
                            } else if (flag.equals("identifier")){
                                if (nextChar == ' ' || i + 1 == linea.length()) {
                                    iterToken += current;
                                    addToken(new Token(numeroLinea, i - iterToken.length() + 1, "id", iterToken));
                                    flag = "";
                                    iterToken = "";}
                                } else {
                                        iterToken += current;
                            }
                        }
                        break;
                    }

                    // Si el carácter actual es un _ seguimos contruyendo el string o identificador
                    if (currentChar == '_') {
                        if (flag.equals("identifier") || flag.equals("stringIter")) {
                            iterToken += current;
                        }
                        break;
                    }

                    // Nombres de clase
                    if(currentChar >= 65 && currentChar <= 90){ //Letras mayuscula en ASCII
                        if (flag.equals("stringIter")) {
                            iterToken += current;
                        } else{
                            if(flag.equals("identifier")){
                                if (nextChar == ' ' || i + 1 == linea.length()) {
                                    iterToken += current;
                                    addToken(new Token(numeroLinea, i - iterToken.length() + 1, "id", iterToken));
                                    flag = "";
                                    iterToken = "";

                                } else {
                                    iterToken += current;
                                }
                            }
                        }
                        break;
                    }

                    // Si viene un numero entero
                    if (currentChar >= '0' && currentChar <= '9') {
                        if (flag.equals("stringIter")) {
                            iterToken += current;
                        }else if(flag.equals("identifier")){
                            if (nextChar == ' ' || i + 1 == linea.length()) {
                                    iterToken += current;
                                    addToken(new Token(numeroLinea, i - iterToken.length() + 1, "id", iterToken));
                                    flag = "";
                                    iterToken = "";

                                } else {
                                    iterToken += current;
                                }
                        } else {
                            if (!(nextChar >= '0' && nextChar <= '9')) {
                                iterToken += current;
                                addToken(new Token(numeroLinea, i - iterToken.length() + 1, "int", iterToken));
                                flag = "";
                                iterToken = "";

                            } else {
                                if (flag == "") {
                                    flag = "int";
                                    iterToken = current;
                                } else if (flag == "int") {
                                    iterToken += current;
                                }
                            }
                        }
                        break;
                    }
            }
        }
    }
}



