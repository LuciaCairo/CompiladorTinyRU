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
        char [] charCurrent = current.toCharArray();
        if (nextChar == '\'') {
            if (idChar == "CharBlackSlash") {
                if (charCurrent[0]== 't'){
                    addToken(new Token(fila, columna - 3, "char","\\" + current)); // ver si la columna esta bien
                } else if (charCurrent[0]== 'n' ){
                    addToken(new Token(fila, columna - 3, "char","\\" + current));
                } else if (charCurrent[0]== 'v' ){
                    addToken(new Token(fila, columna - 3, "char","\\" + current));
                } else if (charCurrent[0] == 'r') {
                    addToken(new Token(fila, columna - 3, "char","\\" + current));
                }else if (charCurrent[0] == '0') {
                    throw new LexicalErrorException(fila, columna , "Caracter invalido. Los caracteres no permiten caracter null");
                } else {
                    addToken(new Token(fila, columna - 2, "char", current));
                }
            } else if (idChar == "iterChar") {
                addToken(new Token(fila, columna -1, "char", current));
            }

        } else{
            throw new LexicalErrorException(fila, columna, "Caracter mal formado. Se esperaba fin de caracter (') despues de " + current );
        }
        return 0;

    }
    //funcion para contar el largo de los string
    public void limitChar(int num, int fila, int columna) {
        if (num > 1024){
            throw new LexicalErrorException(fila, columna - num, "String supera la cantidad maxima de caracteres en cadenas");
        }
    }


    // Cuando se forma un identificador, verificamos que si es una palabra reservada
    private void isKeyWord(int line, int col, String lexema) {
        switch(lexema) {
            case "struct":
                addToken(new Token(line, col, "keyword_struct", lexema));
                break;
            case "impl":
                addToken(new Token(line, col, "keyword_impl", lexema));
                break;
            case "else":
                addToken(new Token(line, col, "keyword_else", lexema));
                break;
            case "false":
                addToken(new Token(line, col, "keyword_false", lexema));
                break;
            case "if":
                addToken(new Token(line, col, "keyword_if", lexema));
                break;
            case "ret":
                addToken(new Token(line, col, "keyword_ret", lexema));
                break;
            case "while":
                addToken(new Token(line, col, "keyword_while", lexema));
                break;
            case "true":
                addToken(new Token(line, col, "keyword_true", lexema));
                break;
            case "nil":
                addToken(new Token(line, col, "keyword_nil", lexema));
                break;
            case "new":
                addToken(new Token(line, col, "keyword_new", lexema));
                break;
            case "fn":
                addToken(new Token(line, col, "keyword_fn", lexema));
                break;
            case "st":
                addToken(new Token(line, col, "keyword_st", lexema));
                break;
            case "pri":
                addToken(new Token(line, col, "keyword_pri", lexema));
                break;
            case "self":
                addToken(new Token(line, col, "keyword_self", lexema));
                break;
            case "void":
                addToken(new Token(line, col, "type_void", lexema));
                break;
            default:
                addToken(new Token(line, col, "id", lexema));
        }
    }

    // Verificamos si el caracter es una letra, un numero o un guion bajo
    private boolean isCharValid(char c) {
        return ((c >= '0' && c <= '9') ||   // numero
                (c == '_') ||               // guion
                (c >= 65 && c <= 90) ||     // mayuscula
                (c >= 97 && c <= 122));     // minuscula
    }

    // Cuando se forma un nombre de clase, verificamos que si es una clase predefinida
    private void isClass(int line, int col, String lexema) {
        switch(lexema) {
            case "Int":
                addToken(new Token(line, col, "class_Int", lexema));
                break;
            case "Bool":
                addToken(new Token(line, col, "class_Bool", lexema));
                break;
            case "Array":
                addToken(new Token(line, col, "class_Array", lexema));
                break;
            case "Char":
                addToken(new Token(line, col, "class_Char", lexema));
                break;
            case "Str":
                addToken(new Token(line, col, "class_String", lexema));
                break;
            case "IO":
                addToken(new Token(line, col, "class_IO", lexema));
                break;
            case "Object":
                addToken(new Token(line, col, "class_Object", lexema));
                break;
            default:
                addToken(new Token(line, col, "struct_name", lexema));
        }
    }

    private void analizarLinea(String linea, int numeroLinea) {
        String flag = ""; // Bandera para indicar que se esta guardando
        String iterToken = "";
        int countStr= 0;

        for (int i = 0; i < linea.length(); i++) {
            // Obtener el carácter actual y el siguiente (si existe)
            char currentChar = linea.charAt(i);
            String current = "" + currentChar;
            char nextChar = (i + 1 < linea.length())? linea.charAt(i + 1) : '\0';

            switch (currentChar) {
                case '\'':
                    if (flag.equals("stringIter")) { //Si se esta formando un string
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
                        iterToken += current;
                    } else if (flag.equals("CharBlackSlash")){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;
                    } else {
                        flag = "iterChar";
                    }
                    break;

                case '\\': // barra invertida
                    if (flag.equals("stringIter") ) {//NULL
                        if (nextChar =='0'){
                            throw new LexicalErrorException(numeroLinea, i, "Caracter invalido. Los Str no permiten caracter null");
                        } else{
                            countStr++;
                            limitChar(countStr,numeroLinea,i);
                            iterToken += current;
                        }
                    } else if (flag.equals("CharBlackSlash")){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;
                    } else if (flag.equals("iterChar")){
                        flag = "CharBlackSlash";
                    } else{
                        throw new LexicalErrorException(numeroLinea, i, "Caracter invalido '\\'" );
                    }
                    break;

                case ' ':
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")){
                        limitChar(countStr,numeroLinea,i);
                        iterToken += current;
                    }
                    break;

                case '"': //si comienza un string o quiero agregar un char
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;
                    }else {
                        if (flag.equals("stringIter")) { //si recibo la ultima " y cierro el string
                            addToken(new Token(numeroLinea, i - iterToken.length() -1, "str", iterToken));
                            flag = "";
                            iterToken = "";
                            break;

                        } else if (flag.equals("")) { //abro string
                            flag = "stringIter";

                        }
                    }
                    break;

                case '+': // Operador suma
                    System.out.println(flag);
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) { //Si se esta formando un string
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if(flag.equals("stringIter")) { //Si se esta formando un string
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
                        iterToken += current;
                    } else {
                        addToken(new Token(numeroLinea, i, "braces_open", current));
                    }
                    break;

                case '}': // Llave cerrada
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    } else if (flag.equals("stringIter")) {
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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

                case '|': // Operador OR
                    if(flag == "CharBlackSlash" || flag == "iterChar"){
                        isChar(flag,nextChar,numeroLinea,i,current);
                        flag = "";
                        i++;

                    }else if (flag.equals("stringIter")) {
                        countStr++;
                        limitChar(countStr,numeroLinea,i);
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
                    if (currentChar >= 97 && currentChar <= 122) { // Letras minusculas en ASCII
                        if(flag == "CharBlackSlash" || flag == "iterChar"){
                            isChar(flag,nextChar,numeroLinea,i,current);
                            flag = "";
                            i++;
                        } else if (flag.equals("stringIter")) {
                            iterToken += current;
                        } else {
                            if (flag == "") { // Llega la primera letra del identificador
                                if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                    isKeyWord(numeroLinea, i, current);
                                    flag = "";
                                    iterToken = "";
                                } else {
                                    flag = "identifier";
                                    iterToken += current;
                                }
                            } else {
                                if (flag.equals("identifier")) {
                                    if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                        iterToken += current;
                                        isKeyWord(numeroLinea,i - iterToken.length() + 1, iterToken);
                                        flag = "";
                                        iterToken = "";
                                    } else {
                                        iterToken += current;
                                    }
                                } else {
                                    if (flag.equals("struct")) {
                                        if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                            iterToken += current;
                                            isClass(numeroLinea,i - iterToken.length() + 1, iterToken);
                                            flag = "";
                                            iterToken = "";
                                        } else {
                                            iterToken += current;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }

                    // Si el carácter actual es un _ seguimos contruyendo el string o identificador o nombre de clase
                    else if (currentChar == '_') {
                        if (flag.equals("stringIter")) {
                            iterToken += current;
                        } else if(flag.equals("struct")){
                            if(nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                // Error: Los nombres de clase no debe terminar con _
                                throw new LexicalErrorException(numeroLinea, i - iterToken.length(), "Los nombres de" +
                                        " clase deben finalizar con letra. " + iterToken+current + " es invalido " );
                            } else {
                                iterToken += current;
                            }
                        } else if(flag.equals("identifier")){
                            if(nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                isKeyWord(numeroLinea, i - iterToken.length(),iterToken+current);
                                flag = "";
                                iterToken = "";
                            } else {
                                iterToken += current;
                            }
                        } else{
                            throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido '" + current + "'" );
                        }
                        break;
                    }

                    // Nombres de clase
                    else if (currentChar >= 65 && currentChar <= 90) { //Letras mayuscula en ASCII
                        if(flag == "CharBlackSlash" || flag == "iterChar"){
                            isChar(flag,nextChar,numeroLinea,i,current);
                            flag = "";
                            i++;
                        } else if (flag.equals("stringIter")) {
                            iterToken += current;
                        } else {
                            if (flag == "") { // Llega la primera letra del nombre de clase
                                if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                    isClass(numeroLinea, i ,current);
                                    flag = "";
                                    iterToken = "";
                                } else {
                                    flag = "struct";
                                    iterToken += current;
                                }
                            } else {
                                if (flag.equals("identifier")) {
                                    if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                        iterToken += current;
                                        isKeyWord(numeroLinea,i - iterToken.length() + 1, iterToken);
                                        flag = "";
                                        iterToken = "";
                                    } else {
                                        iterToken += current;
                                    }
                                } else if (flag.equals("struct")) {
                                    if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                        iterToken += current;
                                        isClass(numeroLinea,i - iterToken.length() + 1, iterToken);
                                        flag = "";
                                        iterToken = "";
                                    } else {
                                        iterToken += current;
                                    }
                                }
                            }
                        }
                        break;
                    }

                    // Si viene un numero entero
                    else if (currentChar >= '0' && currentChar <= '9') {
                        if(flag == "CharBlackSlash" || flag == "iterChar"){
                            isChar(flag,nextChar,numeroLinea,i,current);
                            flag = "";
                            i++;

                        } else if (flag.equals("stringIter")) { //si se esta formando string
                            countStr++;
                            limitChar(countStr,numeroLinea,i);
                            iterToken += current;
                        } else if (flag.equals("identifier")) {
                            if (nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                iterToken += current;
                                isKeyWord(numeroLinea, i - iterToken.length() + 1, iterToken);
                                flag = "";
                                iterToken = "";

                            } else {
                                iterToken += current;
                            }
                        } else if(flag.equals("struct")){
                            if(nextChar == ' ' || i + 1 == linea.length() || !isCharValid(nextChar)) {
                                // Error: Los nombres de clase no debe terminar con numero
                                throw new LexicalErrorException(numeroLinea, i - iterToken.length(), "Los nombres de" +
                                        " clase deben finalizar con letra. " + iterToken+current + " es invalido " );
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
                            break;
                        }
                    }

                    else if ((currentChar >= '\u4E00' && currentChar <= '\u9FFF') ||
                            (currentChar >= '\u3400' && currentChar <= '\u4DBF')){
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido '" + current + "'" );
                      // alfabeto Hangeul
                    } else if ((currentChar >= '\uAC00' && currentChar <= '\uD7A3') ||
                            (currentChar >= '\u3131' && currentChar <= '\u318E')){
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido '" + current + "'" );
                        //alfabeto griego
                    } else if ((currentChar>= '\u0391' && currentChar <= '\u03A1') ||
                            (currentChar >= '\u03A3' && currentChar <= '\u03A9') ||
                            (currentChar >= '\u03B1' && currentChar <= '\u03C1') ||
                            (currentChar >= '\u03C3' && currentChar <= '\u03C9')) {
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido '" + current + "'" );
                    } else{
                        if(flag == "CharBlackSlash" || flag == "iterChar"){
                            isChar(flag,nextChar,numeroLinea,i,current);
                            flag = "";
                            i++;

                        }else if (flag.equals("stringIter")) {
                            countStr++;
                            limitChar(countStr,numeroLinea,i);
                            iterToken += current;
                        }else
                        throw new LexicalErrorException(numeroLinea, i, "Caracter Invalido '" + current + "'" );
                    }




            }
        }
        if (flag == "stringIter"){
            throw new LexicalErrorException(numeroLinea, linea.length(), "String mal formado. Se esperaba un fin de string ");
        }
        if (flag == "CharBlackSlash" || flag == "iterChar"){
            throw new LexicalErrorException(numeroLinea, linea.length(), "Caracter mal formado. Se esperaba un fin de caracter ' ");
        }
    }
}



