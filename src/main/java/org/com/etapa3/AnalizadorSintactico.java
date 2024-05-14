package org.com.etapa3;
import org.com.etapa3.ArbolAST.*;
import org.com.etapa3.ClasesSemantico.*;

import java.io.File;

public class AnalizadorSintactico {

    private static AnalizadorLexico l;
    private static AnalizadorSemantico s;
    private static Token currentToken;
    private static boolean flagMatch = false;
    private static boolean isStart = false;
    private static int isConstr = 0;
    private static boolean isLocal = false;
    private static TablaSimbolos ts;
    private static AST ast;
    public static void main(String[] args) {
        /*if (args.length < 1) {
            System.out.println("ERROR: Debe proporcionar el nombre del archivo fuente.ru como argumento");
            System.out.println("Uso: java -jar etapa3.jar <ARCHIVO_FUENTE> ");
            return;
        }*/

        //String input = args[0];
        String input = "C:\\Users\\Luci\\Documents\\Ciencias de la Computacion\\Compiladores\\CompiladorTinyRU\\src\\main\\java\\org\\com\\etapa3\\prueba.ru";
        String fileName;

        // Obtener el nombre del archivo
        File inputFile = new File(input);
        fileName = inputFile.getName();

        // Verificar existencia del archivo
        File file = new File(input);
        if (!file.exists()) {
            System.out.println("ERROR: El archivo fuente '" + fileName + "' no existe.");
            return;
        }

        // Verificar si el archivo no está vacío
        if (file.length() == 0) {
            System.out.println("ERROR: El archivo fuente '" + fileName + "' está vacío.");
            return;
        }

        // Verificar extensión del archivo
        if (!input.endsWith(".ru")) {
            System.out.println("ERROR: El archivo fuente debe tener la extensión '.ru'.");
            return;
        }

        l = new AnalizadorLexico();
        ts = new TablaSimbolos();
        ast = new AST();
        s = new AnalizadorSemantico(ts);
        try {
            l.analyzeFile(input);
            // Comenzar el análisis sintáctico desde el símbolo inicial ⟨program⟩
            if(l.countTokens() <= 0) { // No hay tokens
                currentToken = new Token(0, 0, "EOF", "EOF");
            } else {
                currentToken = l.nextToken();
            }
            program();      // Analisis Sintactico y Tabla de Simbolos
            s.checkDecl();  // Chequeo de Declaraciones
            //String json = ts.printJSON_Tabla(fileName);
            //ts.saveJSON(json, fileName + ".json");
            String json = ast.printJSON_Arbol(fileName);
            ast.saveJSON(json, fileName + ".json");
            System.out.println("CORRECTO: SEMANTICO - SENTENCIAS\n");

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (SyntactErrorException e) {
            System.out.println("ERROR: SINTACTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (SemantErrorException e) {
            System.out.println("ERROR: SEMANTICO \n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para emparejar el token actual con un token esperado
    private static void match(String expectedToken) {
        if(!flagMatch){
            if (currentToken.getLexema().equals(expectedToken) ||
                    currentToken.getName().equals(expectedToken)) {
                advance();
            } else {
                throw new SyntactErrorException(currentToken.getLine(), currentToken.getCol(),
                        "Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getLexema(),"match");
            }
        }else{
            flagMatch = false;
        }
    }

    // Método para avanzar al siguiente token
    private static void advance() {
        if(l.countTokens() <= 0){ // No hay mas tokens
            currentToken = new Token(0, 0, "EOF", "EOF");
        } else {
            currentToken = l.nextToken();
        }
    }

    private static void program() throws Exception {
        if (currentToken.getLexema().equals("struct") ||
                currentToken.getLexema().equals("impl")){
            definiciones();
            isStart = true;
            start();
        } else if(currentToken.getLexema().equals("start")) {
            isStart = true;
            start();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: struct, impl o start. Se encontró: " + currentToken.getLexema(),
                    "program");
        }
        match("EOF");
    }

    private static void start() {
        EntradaStruct e = new EntradaStruct(currentToken.getLine(), currentToken.getCol());
        ts.setCurrentStruct(e);
        ts.insertStruct_struct(e, currentToken);
        match("start");

        // Construccion AST
        NodoStruct nodo = new NodoStruct(currentToken.getLine(), currentToken.getCol(), "START");
        ast.setCurrentStruct(nodo); // Actualizo el struct actual
        ast.getProfundidad().push(nodo);
        ast.insertStruct("START",nodo); // Inserto el struct en el AST
        bloqueMetodo();
    }

    private static void definiciones() throws Exception {
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

    private static void definiciones1() throws Exception {
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

    private static void struct(){
        match("struct");
        String nombre = currentToken.getLexema();
        match("struct_name");
        // Construccion TS
        EntradaStruct e;
        if(!(ts.searchStruct(nombre))){
            e = new EntradaStruct(nombre, currentToken.getLine(), currentToken.getCol());
        } else {
            e = ts.getStruct(nombre);
        }
        ts.setCurrentStruct(e);
        struct1();
    }

    private static void struct1() {
        if (currentToken.getLexema().equals(":")) {
            String nombre_ancestro = herencia();
            ts.getCurrentStruct().setHerencia(nombre_ancestro);
            ts.insertStruct_struct(ts.getCurrentStruct(), currentToken);
            ts.getCurrentStruct().sethaveStruct(true);
            match("{");
            struct2();
        } else if (currentToken.getLexema().equals("{")) {
            ts.getCurrentStruct().setHerencia("Object");
            ts.insertStruct_struct(ts.getCurrentStruct(), currentToken);
            ts.getCurrentStruct().sethaveStruct(true);
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
                currentToken.getName().equals("struct_name") ||
                currentToken.getLexema().equals("Array")) {
            atributos();
            match("}");
        } else if (currentToken.getLexema().equals("}")) {
            match("}");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct, pri o '}'. " +
                            "Se encontró: " + currentToken.getLexema(),
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
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct, " +
                            "pri o '}'. Se encontró: " + currentToken.getLexema(),
                    "atributos1");
        }
    }

    private static void impl() {
        match("impl");
        String nombre = currentToken.getLexema();
        match("struct_name");

        // Construccion TS
        EntradaStruct e;
        if(!(ts.searchStruct(nombre))){
            e = new EntradaStruct(nombre, currentToken.getLine(), currentToken.getCol());
        } else {
            e = ts.getStruct(nombre);
        }
        ts.setCurrentStruct(e);
        ts.insertStruct_impl(ts.getCurrentStruct(), currentToken);
        ts.getCurrentStruct().sethaveImpl(true);

        // Construccion AST
        NodoStruct nodo = new NodoStruct(currentToken.getLine(), currentToken.getCol(), nombre);
        ast.setCurrentStruct(nodo); // Actualizo el struct actual
        ast.insertStruct(nombre,nodo); // Inserto el struct en el AST
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

    private static String herencia() {
        match(":");
        String nombre = currentToken.getLexema();
        tipo();
        return nombre;
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

        // TS
        EntradaMetodo e = new EntradaMetodo(currentToken.getLine(), currentToken.getCol());
        ts.setCurrentMetod(e);
        ts.getCurrentStruct().insertMetodo("constructor",e);
        ts.getCurrentStruct().setHaveConst(true);
        isConstr = 1;

        // AST
        NodoMetodo nodo = new NodoMetodo(currentToken.getLine(), currentToken.getCol(),
                "constructor");
        ast.setCurrentMetodo(nodo); // Actualizo el metodo actual
        ast.getProfundidad().push(nodo);

        argumentosFormales();
        bloqueMetodo();
        isConstr = 0;

        // Fin del bloque del constructor
        // Inserto el constructor en su struct padre (el actual)
        ast.getCurrentStruct().insertMetodo(currentToken.getLexema(),nodo);
    }

    private static void atributo() {
        if (currentToken.getLexema().equals("pri")){
            visibilidad();
            String tipo = tipo();
            EntradaAtributo e = new EntradaAtributo(currentToken.getLexema(), tipo, false,
                    ts.getCurrentStruct().getAtributos().size(), currentToken.getLine(), currentToken.getCol());
            ts.getCurrentStruct().insertAtributo(currentToken.getLexema(),e);
            ts.setCurrentAtr(e);
            listaDeclaracionVariables();
            match(";");
        } else if (currentToken.getLexema().equals("Str") ||
                    currentToken.getLexema().equals("Bool") ||
                    currentToken.getLexema().equals("Int") ||
                    currentToken.getLexema().equals("Char") ||
                    currentToken.getLexema().equals("Array") ||
                    currentToken.getName().equals("struct_name")) {
            String tipo = tipo();
            EntradaAtributo e = new EntradaAtributo(currentToken.getLexema(), tipo, true, ts.getCurrentStruct().getAtributos().size(),
                    currentToken.getLine(), currentToken.getCol());
            ts.getCurrentStruct().insertAtributo(currentToken.getLexema(),e);
            ts.setCurrentAtr(e);
            listaDeclaracionVariables();
            match(";");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct o pri. " +
                            "Se encontró: " + currentToken.getLexema(),
                    "atributo");
        }
    }

    private static void metodo() {
        if (currentToken.getLexema().equals("st")){
            formaMetodo();
            match("fn");

            // Tabla de Simbolos
            EntradaMetodo e = new EntradaMetodo(currentToken.getLexema(),true,ts.getCurrentStruct().getMetodos().size()- isConstr,
                    currentToken.getLine(), currentToken.getCol());
            ts.setCurrentMetod(e);
            ts.getCurrentStruct().insertMetodo(currentToken.getLexema(),e);

            // AST
            NodoMetodo nodo = new NodoMetodo(currentToken.getLine(), currentToken.getCol(),
                    currentToken.getLexema());
            ast.setCurrentMetodo(nodo); // Actualizo el metodo actual
            ast.getProfundidad().push(nodo);
            match("id");
            argumentosFormales();
            match("->");
            String ret = tipoMetodo();
            ts.getCurrentMetod().setRet(ret);
            bloqueMetodo();

            // Fin del bloque del metodo
            // Inserto el metodo en su struct padre (el actual)
            ast.getCurrentStruct().insertMetodo(currentToken.getLexema(),nodo);

        } else if (currentToken.getLexema().equals("fn")) {
            match("fn");

            // Tabla de Simbolos
            EntradaMetodo e = new EntradaMetodo(currentToken.getLexema(),false,ts.getCurrentStruct().getMetodos().size() - isConstr,
                    currentToken.getLine(), currentToken.getCol());
            ts.setCurrentMetod(e);
            ts.getCurrentStruct().insertMetodo(currentToken.getLexema(),e);

            // AST
            NodoMetodo nodo = new NodoMetodo(currentToken.getLine(), currentToken.getCol(),
                    currentToken.getLexema());
            ast.setCurrentMetodo(nodo); // Actualizo el metodo actual
            ast.getProfundidad().push(nodo);
            match("id");
            argumentosFormales();
            match("->");
            String ret = tipoMetodo();
            ts.getCurrentMetod().setRet(ret);
            bloqueMetodo();

            // Fin del bloque del metodo
            // Inserto el metodo en su struct padre (el actual)
            ast.getCurrentStruct().insertMetodo(currentToken.getLexema(),nodo);

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
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct " +
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
                    "Se esperaba: ;, if, while, ret, self, (, {)" +
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
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct, id " +
                            ",de sentencia (;, if, while, ret, self, (, {, }). " +
                            "Se encontró: " + currentToken.getLexema(),
                    "declaraciones1");
        }
    }

    private static void sentencias() {
        NodoSentencia nodo = sentencia();
        (ast.getProfundidad().peek()).insertSentencia(nodo);
        // Cuando terminaron las sentencias
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
        String tipo = tipo();
        if(isStart){
            EntradaVariable e = new EntradaVariable(currentToken.getLexema(), tipo,
                    ts.getCurrentStruct().getVariables().size(), currentToken.getLine(), currentToken.getCol());
            ts.getCurrentStruct().insertVariable(currentToken.getLexema(),e);
            ts.setCurrentVar(e);
        } else{
            EntradaVariable e = new EntradaVariable(currentToken.getLexema(), tipo,
                    ts.getCurrentMetod().getVariables().size(), currentToken.getLine(), currentToken.getCol());
            ts.getCurrentMetod().insertVariable(currentToken.getLexema(),e);
            ts.setCurrentVar(e);
        }
        isLocal = true;
        listaDeclaracionVariables();
        match(";");
        isLocal = false;
    }

    private static void listaDeclaracionVariables() {
        match("id");
        listaDeclaracionVariables1();
    }

    private static void listaDeclaracionVariables1() {
        if(currentToken.getLexema().equals(",")){
            match(",");
            if(isStart){
                EntradaVariable e = new EntradaVariable(currentToken.getLexema(), ts.getCurrentVar().getType(),
                        ts.getCurrentStruct().getVariables().size(), currentToken.getLine(), currentToken.getCol());
                ts.getCurrentStruct().insertVariable(currentToken.getLexema(), e);
            } else if(!isLocal) {
                EntradaAtributo e = new EntradaAtributo(currentToken.getLexema(), ts.getCurrentAtr().getType(), ts.getCurrentAtr().getPublic(),ts.getCurrentStruct().getAtributos().size(),
                        currentToken.getLine(), currentToken.getCol());
                ts.getCurrentStruct().insertAtributo(currentToken.getLexema(), e);
            } else {
                EntradaVariable e = new EntradaVariable(currentToken.getLexema(), ts.getCurrentVar().getType(),
                        ts.getCurrentMetod().getVariables().size(), currentToken.getLine(), currentToken.getCol());
                ts.getCurrentMetod().insertVariable(currentToken.getLexema(), e);
            }
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
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct o ')'. Se encontró " + currentToken.getLexema(),
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
        String tipo = tipo();
        EntradaParametro e = new EntradaParametro(currentToken.getLexema(), tipo, ts.getCurrentMetod().getParametros().size(),
                currentToken.getLine(), currentToken.getCol());
        ts.getCurrentMetod().insertParametro(currentToken.getLexema(),e);
        match("id");
    }

    private static String tipoMetodo() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            return tipo();
        }else if(currentToken.getLexema().equals("void")){
            match("void");
            return "void";
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array, void) o idStruct. Se encontró " + currentToken.getLexema(),
                    "tipoMetodo");
        }
    }

    private static String tipo() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char")) {
            String tipo = currentToken.getLexema();
            tipoPrimitivo();
            return tipo;
        }else if(currentToken.getLexema().equals("Array")){
            return tipoArreglo();
        }else if(currentToken.getName().equals("struct_name")){
            String tipo = currentToken.getLexema();
            tipoReferencia();
            return tipo;
        }else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array) o idStruct. " +
                            "Se encontró " + currentToken.getLexema(),
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
            match("Char");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array). " +
                            "Se encontró " + currentToken.getLexema(),
                    "tipo");
        }
    }

    private static void tipoReferencia() {
        match("struct_name");
    }
    private static String tipoArreglo() {
        match("Array");
        String tipo = currentToken.getLexema();
        tipoPrimitivo();
        return "Array " + tipo;
    }

    private static NodoSentencia sentencia() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals(";")){
            match(";");
        } else if (currentToken.getLexema().equals("self") ||
                currentToken.getName().equals("id") ){
            NodoSentencia nodo = asignacion(); // AST Asignaciones
            match(";");
            return nodo;
        } else if (currentToken.getLexema().equals("(") ){
            NodoLiteral exp = sentenciaSimple(); // AST Sentencia Simple
            match(";");
            return new NodoExpresion(line,col,"Sentencia Simple",exp.getNodeType(), null,exp);
        } else if (currentToken.getLexema().equals("if")){
            match("if"); // AST IF
            match("(");
            NodoLiteral exp = expresion();
            match(")");
            NodoIf nodoIf= new NodoIf(line,col,exp);
            ast.getProfundidad().push(nodoIf);
            NodoSentencia s = sentencia();
            (ast.getProfundidad().peek()).insertSentencia(s);
            ast.getProfundidad().pop();

            // Caso de que tenga un else
            NodoElse nodoElse = new NodoElse(line,col,nodoIf);
            ast.getProfundidad().push(nodoElse);
            sentencia1(); // AST ELSE
            ast.getProfundidad().pop();
            // Inserto el else en el nodo if
            nodoIf.setNodoElse(nodoElse);
            return nodoIf;
        } else if (currentToken.getLexema().equals("while")){
            match("while"); // AST While
            match("(");
            NodoLiteral exp = expresion();
            match(")");
            NodoWhile nodoW= new NodoWhile(line,col,exp);
            ast.getProfundidad().push(nodoW);
            NodoSentencia s = sentencia();
            (ast.getProfundidad().peek()).insertSentencia(s);
            ast.getProfundidad().pop();
            return nodoW;
        } else if (currentToken.getLexema().equals("{")){
            NodoBloque nodo = new NodoBloque(currentToken.getLine(), currentToken.getCol());
            ast.getProfundidad().push(nodo);
            bloque(); // AST Bloque
            ast.getProfundidad().pop();
            return nodo;
        } else if (currentToken.getLexema().equals("ret")){
            match("ret"); // AST Retorno
            NodoLiteral exp = sentencia2();

            //si el retorno es void.
            if (exp == null){
                //verifico que le metodo, me devuelva void realmente
                if(ts.getCurrentMetod().getRet().equals("void")){

                    //creo un nodo exp, para poder pasarlo al ret.
                    exp = new NodoLiteral(line,col,"Ret;","void",";");
                    return new NodoExpresion(line,col,"Retorno","void", ";", exp);
                }else{
                    throw new SemantErrorException(currentToken.getLine(),
                            currentToken.getCol(),
                            "El retorno del metodo '" + ts.getCurrentMetod().getName()+
                            "' no puede ser '" + "void" + "' porque en su firma esta declarado como "+ts.getCurrentMetod().getRet() ,
                            "sentencia");
                }
                // Verifico q el ret sea igual al de la firma del metodo
            }else {
                if(!(ts.getCurrentMetod().getRet().equals(exp.getNodeType()))){
                    throw new SemantErrorException(currentToken.getLine(),
                            currentToken.getCol(),
                            "El retorno del metodo '" + ts.getCurrentMetod().getName()+
                                    "' no puede ser '" + exp.getNodeType() + "' porque en su firma esta declarado como '"+ts.getCurrentMetod().getRet() +"'",
                            "sentencia");
                }
            }
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba:',', self, id, '(', '{', if, while o ret. " +
                            "Se encontró " + currentToken.getLexema(),
                    "sentencia");
        }
        return null;
    }
    private static void sentencia1() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            NodoSentencia s = sentencia();
            (ast.getProfundidad().peek()).insertSentencia(s);
        }else if(currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("if")||
                currentToken.getLexema().equals("while")||
                currentToken.getLexema().equals("ret")||
                currentToken.getName().equals("id")||
                currentToken.getLexema().equals("self")||
                (currentToken.getLexema().equals("(")) ||
                (currentToken.getLexema().equals("{"))){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: else, if, ';', self, id, '(', '{', while o ret. " +
                            "Se encontró " + currentToken.getLexema(),
                    "sentencia1");
        }
    }

    private static NodoLiteral sentencia2() {
        if (currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-") ||
                currentToken.getLexema().equals("!") ||
                currentToken.getLexema().equals("++") ||
                currentToken.getLexema().equals("--") ||
                currentToken.getLexema().equals("nil") ||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("false") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("new") ||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")){
            NodoLiteral nodo = expresion();
            match(";");
            return nodo;
        }else if(currentToken.getLexema().equals(";")){
            match(";");
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operadores(+,-,++.--), nill, Identificador de Tipo (Str, Char, Int, Bool, Array), id, idStruct, " +
                            "; , self, id , o new. Se encontró " + currentToken.getLexema(),
                    "sentencia2");
        }
        return null;
    }

    private static void bloque() {
        match("{");
        bloque1();
    }

    private static void bloque1() {
        if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("{")){
            sentencias();
            match("}");
        }else if(currentToken.getLexema().equals("}")){
            match("}");
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ';', if, while, ret, id, self, '(', '{' o '}'. " +
                            "Se encontró " + currentToken.getLexema(),
                    "bloque1");
        }
    }

    private static NodoSentencia asignacion() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getName().equals("id")){
            // AST
            // Me encuentro con un identificador
            // Caso especial de start
            if(isStart){
                if(ts.getStruct("start").getVariables().get(currentToken.getLexema()) == null){
                    throw new SemantErrorException(currentToken.getLine(), currentToken.getCol(),
                            "El identificador con el nombre \"" + currentToken.getLexema() + "\" no esta declarado en el metodo \"" +
                                    ts.getCurrentMetod().getName() + "\"", "asignacion");
                } else {
                    String tipoId = ts.getStruct("start").getVariables().get(currentToken.getLexema()).getType();
                    ast.getProfundidad().push(new NodoLiteral(currentToken.getLine(),currentToken.getCol(),
                            currentToken.getLexema(),tipoId, null));
                }
            } else {
                // Verifico si está declarado como variable o como parametro del metodo en la TS
                String tipoId = ts.getCurrentMetod().isDeclared(currentToken.getLexema());
                if(tipoId == null){ // Si no, verifico si esta declarado en los atributos del struct
                    tipoId = ts.getCurrentStruct().isDeclaredAtributo(currentToken.getLexema());
                    if(tipoId == null){ // Si no está declarado se lanza una excepcion de error semantico
                        throw new SemantErrorException(currentToken.getLine(), currentToken.getCol(),
                                "El identificador con el nombre \"" + currentToken.getLexema() + "\" no esta declarado en el metodo \"" +
                                        ts.getCurrentMetod().getName() + "\"", "asignacion");
                    } else {
                        // Si esta declarado se guarda en la pila
                        ast.getProfundidad().push(new NodoLiteral(currentToken.getLine(),currentToken.getCol(),
                                currentToken.getLexema(),tipoId,null));
                    }
                } else { // Si esta declarado se guarda en la pila
                    ast.getProfundidad().push(new NodoLiteral(currentToken.getLine(),currentToken.getCol(),
                            currentToken.getLexema(),tipoId, null));
                }
            }
            NodoLiteral nodoD= (NodoLiteral) accesoVarSimple();
            // Ya puedo guardar el lado izquierdo de la asignacion

            NodoLiteral nodoI = (NodoLiteral) ast.getProfundidad().pop(); // Lo creo y saco de la pila

            if (nodoD != null){
                nodoI = new NodoAcceso(currentToken.getLine(), currentToken.getCol(),
                        nodoI,nodoD, nodoD.getNodeType());
                // El tipo de un acceso es el tipo de su nodo derecho porque:
                // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
                // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
                ast.getProfundidad().push(nodoI);
            }

            match("=");

            // Ahora armo el lado derecho de la asignacion
            nodoD = expresion();
            if(ast.checkTypes(nodoI , nodoD) == null){
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede asignar un " + nodoD.getNodeType() + " a un " + nodoI.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoAsignacion(currentToken.getLine(), currentToken.getCol(), nodoI,nodoD, nodoI.getNodeType());
            // Se termino de armar la asignacion

        }else if(currentToken.getLexema().equals("self")){
            // Lado izquierdo de la asignacion
            ast.getProfundidad().push(new NodoLiteral(ts.getCurrentStruct().getLine(),ts.getCurrentStruct().getCol(),
                    ts.getCurrentStruct().getName(),ts.getCurrentStruct().getName(),null));
            NodoLiteral nodoD= (NodoLiteral) accesoSelfSimple();
            NodoLiteral nodoI = (NodoLiteral) ast.getProfundidad().pop();

            if (nodoD != null){
                nodoI = new NodoAcceso(currentToken.getLine(), currentToken.getCol(),
                        nodoI,nodoD, nodoD.getNodeType());
                // El tipo de un acceso es el tipo de su nodo derecho porque:
                // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
                // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
                ast.getProfundidad().push(nodoI);
            }

            match("=");

            // Ahora armo el lado derecho de la asignacion
            nodoD = expresion();
            if(ast.checkTypes(nodoI , nodoD) == null){
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede asignar un " + nodoD.getNodeType() + " a un " + nodoI.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoAsignacion(currentToken.getLine(), currentToken.getCol(),
                    nodoI,nodoD, nodoI.getNodeType()); // Se termino de armar la asignacion
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: self o id. Se encontró " + currentToken.getLexema(),
                    "asignacion");
        }
    }

    private static NodoSentencia accesoVarSimple() {
        match("id");
        return accesoVarSimple1();
    }

    private static NodoSentencia accesoVarSimple1() {
        if (currentToken.getLexema().equals(".")){
           return encadenadosSimples();
        }else if(currentToken.getLexema().equals("[")){
            String[] palabras = (ast.getProfundidad().peek().getNodeType()).split(" ");
            String isArray = palabras[0];
            if(!isArray.equals("Array")){
                throw new SyntactErrorException(currentToken.getLine(),
                        currentToken.getCol(),
                        "\"" + ast.getProfundidad().peek().getName() +"\" no es un array por lo que no se puede acceder a un indice",
                        "accesoVarSimple1");
            }
            match("[");
            NodoLiteral nodo = expresion();
            match("]");
            return nodo;
        } else if(currentToken.getLexema().equals("=")) {
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '.', '[' o =. Se encontró " + currentToken.getLexema(),
                    "accesoVarSimple1");
        }
        return null;
    }

    private static NodoSentencia encadenadosSimples() {
       NodoLiteral nodoI = (NodoLiteral) encadenadoSimple();
       ast.getProfundidad().push(nodoI);
       NodoLiteral nodoD = (NodoLiteral) encadenadosSimples1();
       if(nodoD==null){
           ast.getProfundidad().pop();
           return nodoI;
       }
       ast.getProfundidad().pop();
       return new NodoAcceso(currentToken.getLine(), currentToken.getCol(), nodoI, nodoD, nodoD.getNodeType());
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
    }

    private static NodoSentencia encadenadosSimples1() {
        if (currentToken.getLexema().equals(".")){
            return encadenadosSimples();
        }else if(currentToken.getLexema().equals("=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '.' o '='. Se encontró " + currentToken.getLexema(),
                    "encadenadosSimples1");
        }
        return null;
    }

    private static NodoSentencia accesoSelfSimple() {
        match("self");
        return accesoSelfSimple1();
    }

    private static NodoSentencia accesoSelfSimple1() {
        if (currentToken.getLexema().equals(".")){
            return encadenadosSimples();
        }else if(currentToken.getLexema().equals("=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '.' o '='. Se encontró " + currentToken.getLexema(),
                    "accesoSelfSimple1");
        }
        return null;
    }

    private static NodoSentencia encadenadoSimple() {
        match(".");
        // Verifico si el struct existe
        if (ts.getStruct(ast.getProfundidad().peek().getNodeType()) != null) {
            // Verifico si el atributo esta declarado en la clase
            if ((ts.getStruct(ast.getProfundidad().peek().getNodeType())).getAtributo(currentToken.getLexema()) == null) {
                throw new SyntactErrorException(currentToken.getLine(),
                        currentToken.getCol(),
                        "El atributo " + currentToken.getLexema() + " no existe en el struct " + ast.getProfundidad().peek().getNodeType() + " . Por lo tanto no se puede acceder a el",
                        "encadenadoSimple");
            }
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    ast.getProfundidad().peek().getName() + " no es un struct por lo " +
                            "que no se puede usar para realizar un acceso a un atributo",
                    "encadenadoSimple");
        }
        String tipoId = (ts.getStruct(ast.getProfundidad().peek().getNodeType())).getAtributo(currentToken.getLexema()).getType();
        NodoLiteral nodo= new NodoLiteral(currentToken.getLine(), currentToken.getCol(), currentToken.getLexema(),tipoId, null);
        match("id");
        return nodo;
    }

    private static NodoLiteral sentenciaSimple() {
        match("(");
        NodoLiteral exp = expresion();
        match(")");
        return exp;
    }

    private static NodoLiteral expresion() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Armamos la expresion (unaria o binaria)
        NodoLiteral nodoI = expAnd();

        NodoLiteral nodoD = expresion1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria
        if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL
            throw new SemantErrorException(line, col,
                    "Incompatibilidad de tipos. No se puede realizar un or entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                    "expCompuesta1");
        }
        return new NodoExpBin(line, col, nodoI,"||", nodoD, "Bool");
    }

    private static NodoLiteral expresion1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("||")){
            match("||");
            // Armamos la expresion (unaria o binaria)
            NodoLiteral nodoI = expAnd();
            NodoLiteral nodoD = expresion1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria
            if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar un or entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoExpBin(line, col, nodoI,"||", nodoD,"Bool");
        }else if(currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '||', ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expresion1");
        }
        return null;
    }

    private static NodoLiteral expAnd() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Armamos la expresion (unaria o binaria)
        NodoLiteral nodoI = expIgual();
        NodoLiteral nodoD = expAnd1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria
        if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
            throw new SemantErrorException(line, col,
                    "Incompatibilidad de tipos. No se puede realizar un and entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                    "expCompuesta1");
        }
        return new NodoExpBin(line, col, nodoI,"&&", nodoD, "Bool");
    }

    private static NodoLiteral expAnd1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("&&")){
            match("&&");
            // Armamos la expresion (unaria o binaria)
            NodoLiteral nodoI = expIgual();
            NodoLiteral nodoD = expAnd1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria
            if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar un and entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoExpBin(line, col, nodoI,"&&", nodoD, "Bool");
        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '&&','||', ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expAnd1");
        }
        return null;
    }

    private static NodoLiteral expIgual() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();

        NodoLiteral nodoI = expCompuesta();
        NodoLiteral nodoD = expIgual1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
            throw new SemantErrorException(line, col,
                    "Incompatibilidad de tipos. No se puede realizar una comparacion entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                    "expCompuesta1");
        }
        return new NodoExpBin(line,col,nodoI,"==",nodoD, "Bool");

    }

    private static NodoLiteral expIgual1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")){
            String op = currentToken.getLexema();
            opIgual();

            NodoLiteral nodoI = expCompuesta();
            NodoLiteral nodoD = expIgual1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria
            if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar una comparacion entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoExpBin(line, col, nodoI,op, nodoD,"Bool");


        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico ('&&','||', '==','!='), ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expIgual1");
        }
        return null;
    }

    private static NodoLiteral expCompuesta() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();

        NodoLiteral nodoI = expAd();
        String op = currentToken.getLexema();
        NodoLiteral nodoD = expCompuesta1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        if(ast.checkTypes(nodoI , nodoD) == null){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
            throw new SemantErrorException(line, col,
                    "Incompatibilidad de tipos. No se puede realizar una comparacion entre un " + nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                    "expCompuesta1");
        }
        return new NodoExpBin(line, col, nodoI, op, nodoD,"Bool");
    }

    private static NodoLiteral expCompuesta1() {
        if (currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")){
            opCompuesto();
            return expAd();
        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico ('&&','||', '==', '!=', '>','|>=', '<', '<='), " +
                            "')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expCompuesta1");
        }
        return null;
    }

    private static NodoLiteral expAd() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        NodoLiteral nodoI = expMul();
        String op = currentToken.getLexema();
        NodoLiteral nodoD = expAd1();

        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        if(!(ast.checkTypes(nodoI , nodoD).equals("Int"))){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?

            throw new SemantErrorException(line, col,
                    "Incompatibilidad de tipos. No se puede realizar una operacion de " + op + " entre un "+ nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                    "expCompuesta1");
        }
        return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");
    }

    private static NodoLiteral expAd1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-")){
            opAd();
            NodoLiteral nodoI = expMul();

            String op = currentToken.getLexema();
            NodoLiteral nodoD = expAd1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria

            if(!(ast.checkTypes(nodoI , nodoD) == "Int")){ // SE PUEDE CON ALGO QUE NO SEA BOOL ?
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar una operacion de " + op + "entre un "+ nodoD.getNodeType() + " y un " + nodoI.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");

        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "operador logico ('&&','||', '==', '!=', '>','|>=', '<', '<='), " +
                            "+, -, ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expAd1");
        }
        return null;
    }

    private static NodoLiteral expMul() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        NodoLiteral nodoI = expUn();
        String op= currentToken.getLexema();

        NodoLiteral nodoD = expMul1();

        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        if(!(ast.checkTypes(nodoI , nodoD) == "Int")){
            throw new SemantErrorException(line, col,
                    "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" entre un " + nodoI.getNodeType() + " y un " + nodoD.getNodeType(),
                    "expCompuesta1");
        }
        return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");
    }

    private static NodoLiteral expMul1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("*") ||
                currentToken.getLexema().equals("/")||
                currentToken.getLexema().equals("%")){
            opMul();
            NodoLiteral nodoI = expUn();
            String op = currentToken.getLexema();
            NodoLiteral nodoD = expMul1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria
            if(!(ast.checkTypes(nodoI , nodoD) == "Int")){
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" entre un " + nodoI.getNodeType() + " y un " + nodoD.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");

        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico ('&&','||', '==', '!=', '>','|>=', '<', '<='), " +
                            "operador aritmetico ('+','-', '/', '*', '%'), " +
                            "')', ';', ']' o ','. Se encontró " + currentToken.getLexema(),
                    "expMul1");
        }
        return null;
    }

    private static NodoLiteral expUn() {
        //int line = currentToken.getLine();
        //int col = currentToken.getCol();
        // Caso de expresion unaria
        if (currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-") ||
                currentToken.getLexema().equals("!") ||
                currentToken.getLexema().equals("++") ||
                currentToken.getLexema().equals("--")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            String op = currentToken.getLexema(); // operador de la expresion unaria
            opUnario();
            NodoLiteral exp = expUn();
            if(!((exp.getNodeType() == "Bool") && (op == "!"))){
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" con un " + exp.getNodeType(),
                        "expCompuesta1");
            } else if (exp.getNodeType() != "Int") {
                ;
                throw new SemantErrorException(line, col,
                        "Incompatibilidad de tipos. No se puede realizar una operacion de \"" + op + "\" con un " + exp.getNodeType(),
                        "expCompuesta1");
            }
            return new NodoExpUn(line, col, exp.getNodeType() ,exp, op);

        } else if(currentToken.getLexema().equals("nil") ||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("false") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("new") ||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")){
            return operando(); // Devuelve la expresion armada con el operando

        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico ('+','-', '++', '--', '!'), " +
                            "literales, self, id o new. Se encontró " + currentToken.getLexema(),
                    "expUn");
        }
    }

    private static void opIgual() {
        if(currentToken.getLexema().equals("==")){
            match("==");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("==");
        } else if(currentToken.getLexema().equals("!=")){
            match("!=");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("!=");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: == o !=. Se encontró " + currentToken.getLexema(),
                    "opIgual");
        }
    }

    private static void opCompuesto() {
        if(currentToken.getLexema().equals("<")){
            match("<");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("<");
        } else if(currentToken.getLexema().equals(">")){
            match(">");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp(">");
        } else if(currentToken.getLexema().equals("<=")){
            match("<=");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("<=");
        } else if(currentToken.getLexema().equals(">=")){
            match(">=");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp(">=");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operadores compuestos ('>','>=', '<', '<='). Se encontró " + currentToken.getLexema(),
                    "literal");
        }
    }

    private static void opAd() {
        if(currentToken.getLexema().equals("+")){
            match("+");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("+");
        }else if(currentToken.getLexema().equals("-")){
            match("-");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("-");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: + o -. Se encontró " + currentToken.getLexema(),
                    "opAd");
        }
    }

    private static void opUnario() {
        if(currentToken.getLexema().equals("+")){
            match("+");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("+");
        } else if(currentToken.getLexema().equals("-")){
            match("-");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("-");
        } else if(currentToken.getLexema().equals("++")){
            match("++");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("++");
        } else if(currentToken.getLexema().equals("--")){
            match("--");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("--");
        } else if(currentToken.getLexema().equals("!")){
            match("!");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("!");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operadores unarios ('+','-', '++', '--', '!'). Se encontró " + currentToken.getLexema(),
                    "opUnario");
        }
    }

    private static void opMul() {
        if(currentToken.getLexema().equals("*")){
            match("*");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("*");
        } else if(currentToken.getLexema().equals("/")){
            match("/");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("/");
        } else if(currentToken.getLexema().equals("%")){
            match("%");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("%");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: *, / o %. Se encontró " + currentToken.getLexema(),
                    "opMul");
        }
    }

    private static NodoLiteral operando() {
        if (currentToken.getLexema().equals("nil") ||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("false") ||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char")) {
            return literal(); // Devuelve la expresion armada con el literal
        } else if (currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("new") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")) {
            NodoLiteral nodoI = primario();
            int line = currentToken.getLine();
            int col = currentToken.getCol();

            NodoLiteral nodoD = (NodoLiteral)operando1();
            if (nodoD == null) {
                return nodoI;
            }

            return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
            // El tipo de un acceso es el tipo de su nodo derecho porque:
            // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
            // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b

        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: literales, self, id o new. Se encontró " + currentToken.getLexema(),
                    "expUn");
        }
    }

    private static NodoSentencia operando1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico, operador aritmetico, ')', ';', ']', ',' o '.'. Se encontró " + currentToken.getLexema(),
                    "operando1");
        }
        return null;
    }

    private static NodoLiteral literal() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if(currentToken.getLexema().equals("nil")){
            // Llego a un literal nulo que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal nulo","nil","nil");
            match("nil");
            return nodo;
        } else if(currentToken.getLexema().equals("true")){
            // Llego a un literal booleano que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal bool","Bool","true");
            match("true");
            return nodo;
        } else if(currentToken.getLexema().equals("false")){
            // Llego a un literal booleano que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal bool","Bool","false");
            match("false");
            return nodo;
        } else if(currentToken.getName().equals("int")){
            // Llego a un literal entero que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal entero","Int",currentToken.getLexema());
            match("int");
            return nodo;
        } else if(currentToken.getName().equals("str")){
            // Llego a un literal string que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal str","Str",currentToken.getLexema());
            match("str");
            return nodo;
        } else if(currentToken.getName().equals("char")){
            // Llego a un literal char que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal char","Char",currentToken.getLexema());
            match("char");
            return nodo;
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: literales, nil, true o false. Se encontró " + currentToken.getLexema(),
                    "literal");
        }
    }

    private static NodoLiteral primario() {
        if(currentToken.getLexema().equals("(")){
           return expresionParentizada();
        } else if(currentToken.getLexema().equals("self")){
            return (NodoLiteral) accesoSelf();
        } else if(currentToken.getName().equals("id")) {

            int line = currentToken.getLine();
            int col = currentToken.getCol();
            String identificador = currentToken.getLexema(); // Antes de que machee
            match("id");
            flagMatch = true;
            String tipoId;
            if(currentToken.getLexema().equals("(")) {

                // Veo si esta declarado como metodo en el struct
                if(!ts.getCurrentStruct().getMetodos().containsKey(identificador)){
                    // Si no esta declarado hay una exception de error semantico
                    throw new SemantErrorException(line,
                            col, "\"" + identificador +
                            "\" no esta declarado como metodo del struct " + ts.getCurrentStruct().getName() ,
                            "encadenadoSimple");
                }
                String typeRet = ts.getCurrentStruct().getMetodos().get(identificador).getRet();
                NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(line, col,ts.getCurrentStruct().getName(),
                        ts.getCurrentStruct().getName(),identificador, typeRet);
                // El tipo de una llamada a un metodo es el tipo del retorno del metodo:
                // Ejemplo: a(), se obtiene algo del tipo de retorno del metodo

                ast.getProfundidad().push(nodo);
                return (NodoLiteral) llamadaMetodo();
            } else{
                // Veo si esta declarado como variable en el metodo
                if(ts.getCurrentMetod().getVariables().containsKey(identificador)){
                    // Guardo su tipo
                    tipoId = (ts.getCurrentMetod()).getVariables().get(identificador).getType();

                    // Si no, veo si esta declarado como parametro en el metodo
                } else if(ts.getCurrentMetod().getParametros().containsKey(identificador)){
                    // Guardo su tipo
                    tipoId = (ts.getCurrentMetod()).getParametros().get(identificador).getType();

                    // Si no, por ultimo veo si esta declarado como atributo en el struct en el metodo
                } else if(ts.getCurrentStruct().getAtributos().containsKey(identificador)){
                    // Guardo su tipo
                    tipoId = (ts.getCurrentStruct().getAtributo(identificador).getType());

                } else { // Si no esta declarado hay una exception de error semantico
                    throw new SemantErrorException(line,
                            col, "\"" + identificador +
                            "\" no esta declarado en el metodo ni como atributo del struct",
                            "encadenadoSimple");
                }

                NodoLiteral nodoI = new NodoLiteral(line, col, identificador,tipoId,null);
                ast.getProfundidad().push(nodoI);
                NodoLiteral nodoD = (NodoLiteral)accesoVar();
                ast.getProfundidad().pop();
                if(nodoD == null){
                    return nodoI;
                }
                return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
                // El tipo de un acceso es el tipo de su nodo derecho porque:
                // Ejemplo 1: a.b, se accede a algo del tipo del atributo b
                // Ejemplo 2: a.b(), se accede a algo del tipo de retorno del metodo b;
            }
        } else if(currentToken.getName().equals("struct_name")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            boolean isPred = false;
            // Veo si existe el struct
            if(ts.getTableStructs().containsKey(currentToken.getLexema())){ // como struct normal
                NodoLiteral nodo = new NodoLiteral(line, col,
                        (ts.getTableStructs().get(currentToken.getLexema()).getName()));
                ast.getProfundidad().push(nodo);
            } else if(ts.getStructsPred().containsKey(currentToken.getLexema())){ // o como struct predefinido
                isPred = true;
                NodoLiteral nodo = new NodoLiteral(line, col,
                        (ts.getStructsPred().get(currentToken.getLexema()).getName()));
                ast.getProfundidad().push(nodo);
            } else {
                // Si no existe hay una exception de error semantico
                throw new SemantErrorException(currentToken.getLine(),
                        currentToken.getCol(), "El struct \"" + currentToken.getLexema() +
                        "\" no existe", "encadenadoSimple");
            }
            return (NodoLiteral) llamadaMetodoEstatico(isPred);

        } else if(currentToken.getLexema().equals("new")){
            return (NodoLiteral) llamadaConstructor();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: id, '(', self o new. Se encontró " + currentToken.getLexema(),
                    "primario");
        }
    }

    private static NodoLiteral expresionParentizada() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Lado izquierdo del acceso
        match("(");
        NodoLiteral exp = expresion();
        ast.getProfundidad().push(exp);
        match(")");
        NodoExpresion nodoI = new NodoExpresion(line, col,"Expresion Parentizada", exp.getNodeType(),null, exp);
        // El tipo de una expresion parentizada es el tipo de su expresion:
        // Ejemplo: (exp) se accede a algo del tipo de su expresion dentro de los ()

        // Lado derecho del acceso
        NodoLiteral nodoD = (NodoLiteral) expresionParentizada1();
        ast.getProfundidad().pop();
        if (nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line,col,nodoI,nodoD, nodoD.getNodeType());
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
    }

    private static NodoSentencia expresionParentizada1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        }else if(currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lamda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico,')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "expresionParentizada1");
        }
        return null;
    }
    private static NodoSentencia accesoSelf() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Guardo a self como (nombre: A, type: A), en vez de normalmente : (nombre: a, type: A)
        NodoLiteral nodoI = new NodoLiteral(ts.getCurrentStruct().getLine(),ts.getCurrentStruct().getCol(),
                ts.getCurrentStruct().getName(),ts.getCurrentStruct().getName(),null);
        ast.getProfundidad().push(nodoI);
        match("self");
        NodoLiteral nodoD = (NodoLiteral) accesoSelf1();
        ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI,nodoD, nodoD.getNodeType());
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b);
    }

    private static NodoSentencia accesoSelf1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lamda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico,')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "AccesoSelf1");
        }
        return null;

    }
    private static NodoSentencia accesoVar() {
        match("id");
        return accesoVar1();
    }
    private static NodoSentencia accesoVar1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals("[")){
            String[] palabras = (ast.getProfundidad().peek().getNodeType()).split(" ");
            String isArray = palabras[0];
            String type = palabras[1];
            if(!isArray.equals("Array")){
                throw new SyntactErrorException(currentToken.getLine(),
                        currentToken.getCol(),
                        "\"" + ast.getProfundidad().peek().getName() +"\" no es unno es un array por lo que no se puede acceder a un indice",
                        "accesoVarSimple1");
            }
            match("[");
            NodoLiteral nodoI = expresion();
            match("]");
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            ast.getProfundidad().push(new NodoLiteral(line,col,type));
            NodoLiteral nodoD = (NodoLiteral) accesoVar2();
            ast.getProfundidad().pop();
            if(nodoD == null){
                return nodoI;
            }
            return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
            // El tipo de un acceso es el tipo de su nodo derecho porque:
            // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
            // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b

        }else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "accesoVar1");
        }
        return null;

    }
    private static NodoSentencia accesoVar2() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "accesoVar2");
        }
        return null;
    }
    private static NodoSentencia llamadaMetodo() {
        match("id");
        argumentosActuales();

        // Termino de formarse la llamada al metodo
        NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) ast.getProfundidad().pop();

        // Ahora es el caso de que se quiera acceder a algo con lo que retorna el metodo
        // ejemplo: metodo().acceso
        // Veo que tipo retorna el metodo (para hacer un encadenado debe devolver algo de tipo idStruct)
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        String typeRet = (ts.getStruct(nodoI.getTypeStruct()).getMetodo(nodoI.getMetodo())).getRet();
        ast.getProfundidad().push(new NodoLiteral(line, col, typeRet));
        NodoLiteral nodoD = (NodoLiteral) llamadaMetodo1();
        ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
    }

    private static NodoSentencia llamadaMetodo1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "llamadaMetodo1");
        }
        return null;
    }
    private static NodoSentencia llamadaMetodoEstatico(boolean isPred) {
        match("struct_name");
        match(".");
        // Verifico que el metodo este declarado en el struct
        if(!isPred){
            if(ts.getStruct(ast.getProfundidad().peek().getNodeType()).getMetodos().containsKey(currentToken.getLexema())){
                String typeRet = ts.getStruct(ast.getProfundidad().peek().getNodeType()).getMetodos().get(currentToken.getLexema()).getRet();
                NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(),
                        ast.getProfundidad().peek().getName(), ast.getProfundidad().peek().getNodeType(), currentToken.getLexema(),
                        typeRet);
                // El tipo de una llamada a un metodo es el tipo que retorna el metodo porque:
                // Ejemplo de nodo llamada metodo: a(), el resultado es algo del tipo que retorna el metodo
                ast.getProfundidad().push(nodo);
            }
        } else if(ts.getStructPred(ast.getProfundidad().peek().getNodeType()).getMetodos().containsKey(currentToken.getLexema())){
            String typeRet = ts.getStructPred(ast.getProfundidad().peek().getNodeType()).getMetodos().get(currentToken.getLexema()).getRet();
            NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(),
                    ast.getProfundidad().peek().getName(), ast.getProfundidad().peek().getNodeType(), currentToken.getLexema()
                    ,typeRet);
            // El tipo de una llamada a un metodo es el tipo que retorna el metodo porque:
            // Ejemplo de nodo llamada metodo: a(), el resultado es algo del tipo que retorna el metodo
            ast.getProfundidad().push(nodo);

        } else {
            throw new SemantErrorException(currentToken.getLine(),
                    currentToken.getCol(), "\"" + currentToken.getLexema() +
                    "\" no esta declarado como metodo en el struct",
                    "encadenadoSimple");
        }

        NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) llamadaMetodo();
        ast.getProfundidad().pop();

        // Ahora es el caso de que se quiera acceder a algo con lo que retorna el metodo
        // ejemplo: IO.metodo().acceso
        // Veo que tipo retorna el metodo (para hacer un encadenado debe devolver algo de tipo idStruct)
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        String typeRet = (ts.getStruct(nodoI.getTypeStruct()).getMetodo(nodoI.getMetodo())).getRet();
        ast.getProfundidad().push(new NodoLiteral(line, col, typeRet));
        NodoLiteral nodoD = (NodoLiteral) llamadaMetodoEstatico1();
        ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b



    }
    private static NodoSentencia llamadaMetodoEstatico1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "llamadaMetodoEstatico1");
        }
        return null;
    }

    private static NodoSentencia llamadaConstructor() {
        match("new");
        return llamadaConstructor1();
    }
    private static NodoSentencia llamadaConstructor1() {
        if (currentToken.getName().equals("struct_name")){ // a = new Fibonacci();
            String type = currentToken.getLexema();
            ast.getProfundidad().push(new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(), currentToken.getLexema(),
                    currentToken.getLexema(), "constructor", currentToken.getLexema()));
            // El tipo de un constructor es el tipo de su struct:
            // Ejemplo: a = new Fibonacci(), se crea una instancia de tipo Fibonacci

            match("struct_name");
            argumentosActuales();

            // Termino de formarse la llamada al metodo
            NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) ast.getProfundidad().pop();

            // Ahora es el caso de que se quiera acceder a algo del struct
            // ejemplo: new A().acceso
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            ast.getProfundidad().push(new NodoLiteral(line, col,type));
            NodoLiteral nodoD = (NodoLiteral) llamadaConstructor2();
            ast.getProfundidad().pop();
            if(nodoD == null){
                return nodoI;
            }
            return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
            // El tipo de un acceso es el tipo de su nodo derecho porque:
            // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
            // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b

        } else if(currentToken.getLexema().equals("Str")||
                currentToken.getLexema().equals("Bool")||
                currentToken.getLexema().equals("Int")||
                currentToken.getLexema().equals("Char")){
            NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(),
                    currentToken.getLexema(), currentToken.getLexema(), "constructor", "Array " + currentToken.getLexema());
            // El tipo de un constructor de Array es del tipo Array + (Str, Bool, Int o Char segun corrresponda):
            // Ejemplo: a = new String[6], se crea una instancia de un array de strings
            tipoPrimitivo();
            match("[");
            nodo.insertArgumento(expresion());
            match("]");
            return nodo;
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                currentToken.getCol(),
                "Se esperaba: un tipo primitivo o un id struct. Se encontró " + currentToken.getLexema(),
                "llamadaConstructor1");
        }
    }

    private static NodoSentencia llamadaConstructor2() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "llamadaConstructor2");
        }
        return null;
    }

    private static void argumentosActuales() {
        match("(");
        argumentosActuales1();

    }
    private static void argumentosActuales1() {
        if (currentToken.getLexema().equals("new")||
                currentToken.getLexema().equals("self")||
                currentToken.getLexema().equals("(")||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char")||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")||
                currentToken.getLexema().equals("false")||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("nil")||
                currentToken.getLexema().equals("--") ||
                currentToken.getLexema().equals("++") ||
                currentToken.getLexema().equals("!") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+")){
            listaExpresiones();
            match(")");
        } else if (currentToken.getLexema().equals(")")){
            match(")");
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ')' ó '+' ó '-' ó '!' ó '++' ó '--' ó 'nill' ó 'true' ó 'false' ó 'int' ó 'str' ó 'char' ó '(' ó 'self' ó id ó idstruct ó new. Se encontró " + currentToken.getLexema(),
                    "argumentosActuales1");
        }
    }

    private static void listaExpresiones() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Armamos la expresion (unaria o binaria)
        NodoLiteral nodoI = expAnd();
        NodoLiteral nodoD = expresion1();
        if (nodoD == null) { // No hay lado derecho entonces es unaria
            ((NodoLlamadaMetodo) ast.getProfundidad().peek()).insertArgumento(nodoI);
        }else{
            ((NodoLlamadaMetodo) ast.getProfundidad().peek()).insertArgumento(new NodoExpBin(line, col, nodoI,"||",
                    nodoD, "Bool"));
        }
        listaExpresiones1();
    }

    private static void listaExpresiones1() {
        if (currentToken.getLexema().equals(",")){
            match(",");
            listaExpresiones();
        } else if (currentToken.getLexema().equals(")")){
            // lambda
        }else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ',' ó ')' . Se encontró " + currentToken.getLexema(),
                    "listaExpresiones1");
        }
    }

    private static NodoSentencia encadenado() {
        // Verifico si el id desde el que se quiere acceder es un struct
        if (ts.getStruct(ast.getProfundidad().peek().getNodeType()) == null) {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    ast.getProfundidad().peek().getName() + " no es un struct por lo " +
                            "que no se puede usar para realizar un acceso a un atributo",
                    "expresionParentizada1");
        }
        match(".");
        return encadenado1();
    }

    private static NodoSentencia encadenado1() {
        if (currentToken.getName().equals("id")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            String lexema = currentToken.getLexema();
            match("id");
            flagMatch = true;

            if(currentToken.getLexema().equals("(")){

                // Verifico si el id esta declarado como metodo del struct
                if ((ts.getStruct(ast.getProfundidad().peek().getNodeType())).getMetodo(lexema) == null) {
                    throw new SyntactErrorException(currentToken.getLine(),
                            currentToken.getCol(),
                            "El metodo " + lexema + " no existe en el struct " + ast.getProfundidad().peek().getNodeType() +
                                    " . Por lo tanto no se puede acceder a el", "encadenadoSimple");
                }
                String typeRet = (ts.getStruct(ast.getProfundidad().peek().getNodeType())).getMetodo(lexema).getRet();
                ast.getProfundidad().push(new NodoLlamadaMetodo(line, col,ast.getProfundidad().peek().getName(),
                        ast.getProfundidad().peek().getNodeType(),lexema, typeRet));
                // El tipo de una llamada a un metodo es el tipo que retorna ese metodo, porque:
                // Ejemplo de un nodo llamada metodo: a(), el resultado es algo del tipo que retorna el metodo
                return llamadaMetodoEncadenado();

            } else {
                // Verifico si el id esta declarado como atributo del struct
                if ((ts.getStruct(ast.getProfundidad().peek().getNodeType())).getAtributo(lexema) == null) {
                    throw new SyntactErrorException(currentToken.getLine(),
                            currentToken.getCol(),
                            "El atributo " + lexema + " no existe en el struct " + ast.getProfundidad().peek().getNodeType() +
                                    " . Por lo tanto no se puede acceder a el", "encadenadoSimple");
                }
                String tipoId = (ts.getStruct(ast.getProfundidad().peek().getNodeType())).getAtributo(lexema).getType();
                NodoLiteral nodoI = new NodoLiteral(line, col, lexema, tipoId,null);
                ast.getProfundidad().push(nodoI);
                NodoLiteral nodoD = (NodoLiteral) accesoVariableEncadenado();
                ast.getProfundidad().pop();
                if(nodoD == null){
                    return nodoI;
                }
                return new NodoAcceso(line,col,nodoI, nodoD, nodoD.getNodeType());
                // El tipo de un acceso es el tipo de su nodo derecho porque:
                // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
                // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
            }
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: identificador. Se encontró " + currentToken.getLexema(),
                    "encadenado1");
        }
    }

    private static NodoSentencia llamadaMetodoEncadenado() {
        match("id");
        argumentosActuales();
        NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) ast.getProfundidad().pop();
        // Veo que tipo retorna el metodo (para hacer un encadenado debe devolver algo de tipo idStruct)
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        String typeRet = (ts.getStruct(nodoI.getTypeStruct()).getMetodo(nodoI.getMetodo())).getRet();
        ast.getProfundidad().push(new NodoLiteral(line, col, typeRet));
        NodoLiteral nodoD = (NodoLiteral) llamadaMetodoEncadenado1();
        ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
        // El tipo de un acceso es el tipo de su nodo derecho porque:
        // Ejemplo 1: a().b, se accede a algo del tipo del atributo b
        // Ejemplo 2: a().b(), se accede a algo del tipo de retorno del metodo b
    }

    private static NodoSentencia llamadaMetodoEncadenado1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    " llamadaMetodoEncadenado2");
        }
        return null;
    }
    private static NodoSentencia accesoVariableEncadenado() {
        match("id");
        return accesoVariableEncadenado1();
    }
    private static NodoSentencia accesoVariableEncadenado1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if(currentToken.getLexema().equals("[")){
            match("[");
            expresion();
            match("]");
            accesoVariableEncadenado2();

        }else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,'[',']' o ','. Se encontró " + currentToken.getLexema(),
                    "accesoVariableEncadenado1");
        }
        return null;
    }
    private static void accesoVariableEncadenado2() {
        if (currentToken.getLexema().equals(".")){
            encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "accesoVariableEncadenado2");
        }
    }
}
