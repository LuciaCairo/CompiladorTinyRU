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
        if( expectedToken.equals("idMetAt")){
            if (currentToken.getName().equals("id") ||
                    currentToken.getName().equals("struct_name")) {
                advance();
            } else {
                throw new SyntactErrorException(currentToken.getLine(), currentToken.getCol(),
                        "Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getLexema(),"match");
            }
        } else if (currentToken.getLexema().equals(expectedToken) ||
                currentToken.getName().equals(expectedToken)) {
            advance();
        } else {
            throw new SyntactErrorException(currentToken.getLine(), currentToken.getCol(),
                    "Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getLexema(),"match");
        }
    }

    // Método para avanzar al siguiente token
    private static void advance() {
        if(l.countTokens() <= 0){ // No hay mas tokens
            // Aca nose bien que deberia pasar
            // Excepcion ??
        } else {
            currentToken = l.nextToken();
        }
    }

    private static void program() {
        if (currentToken.getLexema().equals("struct") ||
                currentToken.getLexema().equals("impl")){
            definiciones();
            start();
        } else if(currentToken.getLexema().equals("start")) {
            start();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: struct, impl o start. Se encontró: " + currentToken.getLexema(),
                    "program");
        }
    }

    private static void start() {
        match("start");
        bloqueMetodo();
    }

    private static void definiciones() {
        if (currentToken.getLexema().equals("struct")) {
            struct();
            definiciones1();
        } else if (currentToken.getLexema().equals("impl")) {
            impl();
            definiciones1();
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: struct o impl. Se encontró: " + currentToken.getLexema(),
                    "definiciones");
        }
    }

    private static void definiciones1() {
        if (currentToken.getLexema().equals("struct") ||
                currentToken.getLexema().equals("impl")) {
            definiciones();
        } else if(currentToken.getLexema().equals("start")){
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: start, struct o impl. Se encontró: " + currentToken.getLexema(),
                    "definiciones1");
        }
    }

    private static void struct() {
        match("struct");
        match("struct_name");
        struct1();
    }

    private static void struct1() {
        if (currentToken.getLexema().equals(":")) {
            herencia();
            match("{");
            struct2();
        } else if (currentToken.getLexema().equals("{")) {
            match("{");
            struct2();
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ':' o '{'. Se encontró: " + currentToken.getLexema(),
                    "struct1");
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
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo, pri o '}'. Se encontró: " + currentToken.getLexema(),
                    "struct2");
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
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo, pri o '}'. Se encontró: " + currentToken.getLexema(),
                    "atributos1");
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
        if (currentToken.getLexema().equals("st")
                || currentToken.getLexema().equals("fn")
                || currentToken.getLexema().equals(".")) {
            miembros();
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: st, fn, '.' o '}'. Se encontró: " + currentToken.getLexema(),
                    "miembros1");
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
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: st, fn o '.'. Se encontró: " + currentToken.getLexema(),
                    "miembro");
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
            tipo();
            listaDeclaracionVariables();
            match(";");
        } else if (currentToken.getLexema().equals("Str") ||
                    currentToken.getLexema().equals("Bool") ||
                    currentToken.getLexema().equals("Int") ||
                    currentToken.getLexema().equals("Char") ||
                    currentToken.getLexema().equals("Array") ||
                    currentToken.getName().equals("struct_name")) {
                tipo();
                listaDeclaracionVariables();
                match(";");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo o pri. Se encontró: " + currentToken.getLexema(),
                    "atributo");
        }
    }

    private static void metodo() {
        if (currentToken.getLexema().equals("st")){
            formaMetodo();
            match("fn");
            match("idMetAt");
            argumentosFormales();
            match("->");
            tipoMetodo();
            bloqueMetodo();
        } else if (currentToken.getLexema().equals("fn")) {
            match("fn");
            match("struct_name");
            argumentosFormales();
            match("->");
            tipoMetodo();
            bloqueMetodo();
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: st o fn. Se encontró: " + currentToken.getLexema(),
                    "metodo");
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
            bloqueMetodo2();
        } else if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
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
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de tipo " +
                            ",de declaracion (;, if, while, ret, id, self, (, {)" +
                            "o '}'. Se encontró: " + currentToken.getLexema(),
                    "bloqueMetodo1");
        }
    }

    private static void bloqueMetodo2() {
        if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("(")){
                sentencias();
            match("}");
        } else if(currentToken.getLexema().equals("}")) {
            match("}");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: declaracion (;, if, while, ret, id, self, (, {)" +
                            "o '}'. Se encontró: " + currentToken.getLexema(),
                    "bloqueMetodo2");
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
                currentToken.getLexema().equals("}") ||
                currentToken.getName().equals("id")){
            //lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de tipo " +
                            ",de declaracion (;, if, while, ret, id, self, (, {, }). " +
                            "Se encontró: " + currentToken.getLexema(),
                    "declaraciones1");
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
                currentToken.getName().equals("id")){
                sentencias();
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ';', if, while, ret, id, self, '(', '{' o '}'. " +
                            "Se encontró: " + currentToken.getLexema(),
                    "sentencias1");
        }
    }

    private static void declVarLocales() {
        tipo();
        listaDeclaracionVariables();
        match(";");
    }

    private static void listaDeclaracionVariables() {
        match("idMetAt");
        listaDeclaracionVariables1();
    }

    private static void listaDeclaracionVariables1() {
        if(currentToken.getLexema().equals(",")){
            match(",");
            listaDeclaracionVariables();
        }else if(currentToken.getLexema().equals(";")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ',' o ';'. Se encontró " + currentToken.getLexema(),
                    "listaDeclaracionVariables1");
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
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo o ')'. Se encontró " + currentToken.getLexema(),
                    "argumentosFormales1");
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
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ',' o ')'. Se encontró " + currentToken.getLexema(),
                    "listaArgumentosFormales1");
        }
    }

    private static void argumentoFormal() {
        tipo();
        match("idMetAt ");
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
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: identificador de tipo. Se encontró " + currentToken.getLexema(),
                    "tipoMetodo");
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
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba:'Tipo-Primitivo' o 'Arreglo' o 'IDStruc'. Se encontró " + currentToken.getLexema(),
                    "tipo");
        }
    }

    private static void tipoPrimitivo() {
        if (currentToken.getLexema().equals("Str")){
            match("Str");
        } else if (currentToken.getLexema().equals("Bool")){
            match("Bool");
        } else if (currentToken.getLexema().equals("Int")){
            match("Int");
        } else if(currentToken.getLexema().equals("Char")){
            match("Array");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba:'Tipo-Primitivo'. Se encontró " + currentToken.getLexema(),
                    "tipo");
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
        if (currentToken.getLexema().equals(";")){
            match(";");
        } else if (currentToken.getLexema().equals("self") ||
                currentToken.getName().equals("id") ){
            asignacion();
            match(";");
        } else if (currentToken.getLexema().equals("(") ){
            sentenciaSimple();
            match(";");
        } else if (currentToken.getLexema().equals("if")){
            match("if");
            match("(");
            expresion();
            match(")");
            sentencia();
            sentencia1();
        } else if (currentToken.getLexema().equals("while")){
            match("while");
            match("(");
            expresion();
            match(")");
            sentencia();
        } else if (currentToken.getLexema().equals("{")){
            bloque();
        } else if (currentToken.getLexema().equals("ret")){
            match("ret");
            sentencia2();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba:',', self, id, '(', '{', if, while o ret. Se encontró " + currentToken.getLexema(),
                    "sentencia");
        }
    }
    private static void sentencia1() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            sentencia();
        }else if(currentToken.getLexema().equals(")")){ // calcular s(sentencia1)
            // lambda
        }else{

        }
    }

    private static void sentencia2() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            sentencia();
        }else if(currentToken.getLexema().equals(")")){ // calcular s(sentencia1)
            // lambda
        }else{

        }
    }

    private static void expresion() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            sentencia();
        }else if(currentToken.getLexema().equals(")")){ // calcular s(sentencia1)
            // lambda
        }else{

        }
    }

    private static void bloque() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            sentencia();
        }else if(currentToken.getLexema().equals(")")){ // calcular s(sentencia1)
            // lambda
        }else{

        }
    }

    private static void sentenciaSimple() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            sentencia();
        }else if(currentToken.getLexema().equals(")")){ // calcular s(sentencia1)
            // lambda
        }else{

        }
    }


    private static void asignacion() {
        match("Array");
        tipoPrimitivo();
    }
}
