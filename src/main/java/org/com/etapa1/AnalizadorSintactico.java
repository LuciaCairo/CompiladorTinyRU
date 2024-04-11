package org.com.etapa1;

import java.io.File;
import java.io.IOException;

public class AnalizadorSintactico {

    private static AnalizadorLexico l;
    private static Token currentToken;

    public static void main(String[] args) {
        /*if (args.length < 1) {
            System.out.println("ERROR: Debe proporcionar el nombre del archivo fuente.ru como argumento");
            System.out.println("Uso: java -jar etapa1.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]");
            return;
        }*/

        //String input = args[0];
        String input = "C:\\Users\\Luci\\Documents\\Ciencias de la Computacion\\Compiladores\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa1\\prueba.ru";
        // Verificar existencia del archivo
        File file = new File(input);
        if (!file.exists()) {
            System.out.println("ERROR: El archivo fuente '" + input + "' no existe.");
            return;
        }

        // Verificar si el archivo no está vacío
        if (file.length() == 0) {
            System.out.println("ERROR: El archivo fuente '" + input + "' está vacío.");
            return;
        }

        // Verificar extensión del archivo
        if (!input.endsWith(".ru")) {
            System.out.println("ERROR: El archivo fuente debe tener la extensión '.ru'.");
            return;
        }

        l = new AnalizadorLexico();

        try {
            l.analyzeFile(input);
            currentToken = l.nextToken();
            // Comenzar el análisis sintáctico desde el símbolo inicial ⟨program⟩
            program();

            System.out.println("CORRECTO: ANALISIS SINTACTICO \n");

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (SyntactErrorException e) {
            System.out.println("ERROR: SINTACTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");
            System.out.println("FUNCION " + e.functionName() + "\n");

        }
    }

    // Método para emparejar el token actual con un token esperado
    private static void match(String expectedToken) {
        if (currentToken.getLexema().equals(expectedToken) ||
                currentToken.getName().equals(expectedToken)) {
            advance();
        } else {
            throw new SyntactErrorException(currentToken.getLine(), currentToken.getCol(),
                    "Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getLexema(),"match");
        }
    }

    // Método para avanzar al siguiente token
    private static void advance() {
        if (l.countTokens() <= 0){ // No hay mas tokens
            // Aca nose bien que deberia pasar
            // Excepcion ??
        } else {
            currentToken = l.nextToken();
        }
    }

    private static void program() {
        if (currentToken.getLexema().equals("struct") || currentToken.getLexema().equals("impl")){
            definiciones();
            start(); // No se si va start ????
        } else {
            start();
        } // Falta EXCEPCION !!
    }

    private static void start() {
        match("start");
        bloqueMetodo();
    }

    private static void definiciones() {
        if (currentToken.getLexema().equals("struct")) {
            struct();
            definiciones1(); // Aca nose si tambien se llama a definiciones1 ???
        } else if (currentToken.getLexema().equals("impl")) {
            impl();
            definiciones1(); // Aca nose si tambien se llama a definiciones1 ???
        } else {
            System.out.println("Error Sintactico. Se esperaba 'struct' o 'impl'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void definiciones1() {
        if (currentToken.getLexema().equals("struct") || currentToken.getLexema().equals("impl")) {
            definiciones();
        } else {
            // Si no se encuentra ni "struct" ni "impl", se asume que se ha completado la secuencia de definiciones
            // y no se hace nada, ya que ⟨Definiciones1⟩ permite λ (la producción vacía)
            // Esto significa que no hay más definiciones que analizar en este punto.
            // EXCEPCION O NO ES UN ERROR???
        }
    }

    private static void struct() {
        match("struct");
        match("struct_name"); // ??? DOS MATCHS
        struct1();
    }

    private static void struct1() {
        if (currentToken.getLexema().equals(":")) {
            herencia();
            match("{");
            struct2(); // QUE PASA CON STRUCT2? NO SE LLAMA O SI???
        } else if (currentToken.getLexema().equals("{")) {
            match("{");
            struct2();
        } else {
            System.out.println("Error Sintactico. Se esperaba ':' o '{'");
            System.exit(1); // EXCEPCION!!!
            // Lanzar una excepción
        }
    }

    private static void struct2() {
        if (currentToken.getLexema().equals("pri") ||
                currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("struct_name") ||
                currentToken.getLexema().equals("Array")) {
            atributos();
            match("}");
        } else if (currentToken.getLexema().equals("}")) {
            match("}");
        } else {
            System.out.println("Error sintáctico. Se esperaba un TIPO de atributo o '}'");
            System.exit(1); // excepcion !!
        }
    }

    private static void atributos() {
        atributo();
        atributos1();
    }

    private static void atributos1() {
        if (currentToken.getLexema().equals("pri") ||
                currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            atributos();
        } else if (currentToken.getLexema().equals("}")){
            // no se hace nada aca???
        } else{
            System.out.println("Error Sintactico. Se esperaba 'Tipo-Primitivo' o '}' "+". Se encontroó: " + currentToken.getLexema()); //Revisar este error
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void impl() {
        match("impl");
        match("struct_name");
        match("{");
        miembros();
        match("}");
    }

    private static void miembros() {
        miembro();
        miembros1();
    }

    private static void miembros1() {
        if (currentToken.getLexema().equals("pri")
                || currentToken.getLexema().equals("fn")
                || currentToken.getLexema().equals(".")) {
            miembros();
        } else if(currentToken.getLexema().equals("}")){
            // aca nada???
        } else{
            System.out.println("Error Sintactico. Se esperaba 'pri', 'fn', '.' o '}'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void herencia() {
        match(":");
        tipo();
    }

    private static void miembro() {
        if (currentToken.getLexema().equals("st")
                || currentToken.getLexema().equals("fn")){
            metodo();
        } else if(currentToken.getLexema().equals(".")){
            constructor();
        } else{
            System.out.println("Error Sintactico. Se esperaba 'st', 'fn', '.' o '}'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void constructor() {
        match(".");
        argumentosFormales();
        bloqueMetodo();
    }

    private static void atributo() {
        if (currentToken.getLexema().equals("pri")){
            visibilidad();
            tipo(); // se llama tambien ?? quien es B todo o solo visibilidad?
            listaDeclaracionVariables();
            match(";");
        } else if (currentToken.getLexema().equals("Str") ||
                    currentToken.getLexema().equals("Bool") ||
                    currentToken.getLexema().equals("Int") ||
                    currentToken.getLexema().equals("Char") ||
                    currentToken.getLexema().equals("Array") ||
                    currentToken.getName().equals("struct_name")) {
                tipo();
                listaDeclaracionVariables(); // se llama tambien ?? quien es B todo o solo tipo?
                match(";");
        } else {
            System.out.println("Error Sintactico. Se esperaba 'Tipo-Primitivo' o '}' "+". Se encontró: " + currentToken.getLexema()); //Revisar este error
            System.exit(1); // EXCEPCION!!
        }
    }

    private static void metodo() {
        if (currentToken.getLexema().equals("st")){
            formaMetodo(); //B
            match("fn"); //x
            // idMetAt ⟨Argumentos-Formales⟩ “->” ⟨Tipo-Método⟩ ⟨Bloque-Método⟩
            // que hago con el resto de la regla???
        } else if (currentToken.getLexema().equals("fn")) {
            match("fn"); //y
            match("struct_name"); // nose si tambien match o no ???
            argumentosFormales(); // C
            // que hago con el resto de la regla???
            // “->” ⟨Tipo-Método⟩ ⟨Bloque-Método⟩
        } else {
            System.out.println("Error Sintactico."); //Revisar este error
            System.exit(1); // EXCEPCION!!
        }
    }

    private static void visibilidad() {
        match("pri");
    }

    private static void formaMetodo() {
        match("st");
    }

    private static void bloqueMetodo() {
        match("{");
        bloqueMetodo1();
    }

    private static void bloqueMetodo1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            declaraciones();
            bloqueMetodo2(); // esto tambien es B??? se llama?
        } else if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("(")){
            sentencias();
            match("}");
        } else if(currentToken.getLexema().equals("}")) {
            match("}");
        } else{
            System.out.println("Error sintáctico. Se esperaba una declaración o '}'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void bloqueMetodo2() {
        if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("(")){
            sentencias();
            match("}");
        } else if(currentToken.getLexema().equals("}")) {
            match("}");
        } else{
            System.out.println("Error sintáctico. Se esperaba una declaración, una sentencia o '}'. Se encontró: " + currentToken.getLexema());
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void declaraciones() {
        declVarLocales();
        declaraciones1();
    }

    private static void declaraciones1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            declaraciones();

        } else if(currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getName().equals("id")){
            // no hacer nada ???
        } else{
            System.out.println("Error sintáctico. ");
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void sentencias() {
        sentencia();
        sentencias1();
    }

    private static void sentencias1() {
        if(currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getName().equals("struct_name") ||
                currentToken.getName().equals("id")){
            sentencias();
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        }else{
            System.out.println("Error sintáctico. ");
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void declVarLocales() {
        tipo();
        listaDeclaracionVariables();
        match(";");
    }

    private static void listaDeclaracionVariables() {
        match("id");
        listaDeclaracionVariables1();
    }

    private static void listaDeclaracionVariables1() {
        if(currentToken.getLexema().equals(",")){
            match(",");
            listaDeclaracionVariables();
        }else if(currentToken.getLexema().equals(";")){
            //match(";");
            //Preguntar si esto esta bien, porque en teoria segun el algoritmo cuando tenes lamda no tenes que hacer nada
        }else{
            System.out.println("Error Sintactico. Se esperaba ',' o ';'"); //Revisar este error
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void argumentosFormales() {
        match("(");
        argumentosFormales1();
    }

    private static void argumentosFormales1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            listaArgumentosFormales();
            match(")");
        }else if(currentToken.getLexema().equals(")")){
            match(")");
        }else{
            System.out.println("Error Sintactico."); //Revisar este error
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void listaArgumentosFormales() {
        argumentoFormal();
        listaArgumentosFormales1();
    }

    private static void listaArgumentosFormales1() {
        if (currentToken.getLexema().equals(",")){
            match(",");
            listaArgumentosFormales();

        }else if(currentToken.getLexema().equals(")")){
            match(")");
        }else{
            System.out.println("Error Sintactico.");
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void argumentoFormal() {
        tipo();
        match("id");
    }

    private static void tipoMetodo() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            tipo();
        }else if(currentToken.getLexema().equals("void")){
            match("void");
        }else{
            System.out.println("Error Sintactico."); //Revisar este error
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void tipo() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char"))
                {
            tipoPrimitivo();
        }else if(currentToken.getLexema().equals("Array")){
            tipoArreglo();
        }else if(currentToken.getName().equals("struct_name")){
            tipoReferencia();
        }else {
            System.out.println("Error Sintactico. Se esperaba 'Tipo-Primitivo' o 'Arreglo' o 'IDStruc'"); //Revisar este error
            System.exit(1); // EXCEPCION!!!
        }
    }

    private static void tipoPrimitivo() {
        if (currentToken.getLexema().equals("Str")){ // creo que son innecesarios los ifs???
            match("Str");
        } else if (currentToken.getLexema().equals("Bool")){
            match("Bool");
        } else if (currentToken.getLexema().equals("Int")){
            match("Int");
        } else if(currentToken.getLexema().equals("Char")){
            match("Array");
        } else{
            System.out.println("Error Sintactico. Se esperaba 'Tipo-Primitivo'"); //Revisar este error
            System.exit(1); // EXCEPCION!!!
            // Lanzar una excepción en lugar de simplemente imprimir un mensaje de error
        }
    }

    private static void tipoReferencia() {
        match("struct_name");
    }
    private static void tipoArreglo() {
        match("Array");
        tipoPrimitivo();
    }


    private static void sentencia() {
        if (currentToken.getLexema().equals(",")){
            match(";");
        } else if (currentToken.getLexema().equals("self") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("structName") ){ // ACA
            match("Bool");
        } else if (currentToken.getLexema().equals("Int")){
            match("Int");
        } else if(currentToken.getLexema().equals("Char")){
            match("Array");
        } else{
            System.out.println("Error Sintactico. Se esperaba 'Tipo-Primitivo'"); //Revisar este error
            System.exit(1); // EXCEPCION!!!
            // Lanzar una excepción en lugar de simplemente imprimir un mensaje de error
        }
    }
}
